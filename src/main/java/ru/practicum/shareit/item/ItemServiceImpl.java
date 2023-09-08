package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongInputDataException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUpdate;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final CommentRepository commentRepository;
    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item newItem = ItemMapper.toItem(itemDto);
        if (newItem.getRequestId() != null && newItem.getRequestId() != 0) {
            Optional<ItemRequest> request = requestRepository.findById(newItem.getRequestId());
            if (request.isEmpty()) {
                log.info("Невозможно создать item. Указан несуществующий requestId {}.", newItem.getRequestId());
                throw new ValidationException(
                        "Невозможно создать item. Указан несуществующий requestId " + newItem.getRequestId() + ".");
            }
        }
        User owner = userRepository.findById(userId).orElseThrow((
                () -> new NotFoundException("Невозможно создать предмет, владелец с id = " + userId + " не найден.")));
        newItem.setOwner(owner);
        newItem.setAvailable(true);
        Item item = itemRepository.save(newItem);
        log.info("Создан item {}.", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemForUpdate item, long itemId, long userId) {
        Optional<User> owner = userRepository.findById(userId);
        if (owner.isEmpty()) {
            log.info("Невозможно обновить предмет, владелец с id = " + userId + " не найден.");
            throw new NotFoundException("Невозможно обновить предмет, владелец с id = " + userId + " не найден.");
        }
        Optional<Item> oldItem = itemRepository.findById(itemId);
        if (oldItem.isEmpty()) {
            log.info("Не найден itemId с id = {}.", itemId);
            throw new NotFoundException(String.format("Не найден itemId с id = %s.", itemId));
        }
        Item newItem = ItemMapper.toItem(item, itemId);
        newItem.setOwner(owner.get());
        if (newItem.getName() == null) {
            newItem.setName(oldItem.get().getName());
        }
        if (newItem.getDescription() == null) {
            newItem.setDescription(oldItem.get().getDescription());
        }
        if (newItem.getAvailable() == null) {
            newItem.setAvailable(oldItem.get().getAvailable());
        }
        Item updatedItem = itemRepository.save(newItem);
        log.info("Обновлён item {}.", updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemCommentDto getItem(long itemId, long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.info("Невозможно выгрузить item, пользователь с id = " + userId + " не найден.");
            throw new NotFoundException("Невозможно выгрузить item, пользователь с id = " + userId + " не найден.");
        }
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            log.info("Не найден itemId с id = {}.", itemId);
            throw new NotFoundException(String.format("Не найден itemId с id = %s.", itemId));
        }
        BookingForItemDto bookingBefore = null;
        BookingForItemDto bookingAfter = null;
        LocalDateTime dt = LocalDateTime.now();
        if (item.get().getOwner().getId() == userId) {
            bookingBefore = BookingMapper.toBookingForItemDto(
                    bookingRepository.findFirstByItemIdEqualsAndStatusIsNotAndStartBeforeOrderByStartDesc(
                            itemId,
                            BookingStatus.REJECTED,
                            LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond())));
            bookingAfter = BookingMapper.toBookingForItemDto(
                    bookingRepository.findFirstByItemIdEqualsAndStatusIsNotAndStartAfterOrderByStartAsc(
                            itemId,
                            BookingStatus.REJECTED,
                            LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond())));
        }
        List<CommentDto> comments = CommentMapper.toCommentDto(commentRepository.findAllByItemIdEqualsOrderByCreatedDesc(itemId));
        log.info("Выгружен item {}.", item);
        return ItemMapper.toItemCommentDto(item.get(), bookingBefore, bookingAfter, comments);
    }

    @Override
    public List<ItemCommentDto> getItems(long owner, int from, int size) {
        Optional<User> user = userRepository.findById(owner);
        if (user.isEmpty()) {
            log.info("Невозможно выгрузить список item, владелец с id = " + owner + " не найден.");
            throw new NotFoundException("Невозможно выгрузить список item, владелец с id = " + owner + " не найден.");
        }
        Pageable pageParams = PageRequest.of(fromToPage(from, size), size, Sort.by(Sort.Direction.ASC, "id"));
        Page<Item> items = itemRepository.findAllByOwnerIdIsOrderByIdAsc(owner, pageParams);
        List<ItemCommentDto> itemsForOwner = new ArrayList<>();
        LocalDateTime dt = LocalDateTime.now();
        for (Item item : items) {
            BookingForItemDto bookingBefore = BookingMapper.toBookingForItemDto(
                    bookingRepository.findFirstByItemIdEqualsAndStatusIsNotAndStartBeforeOrderByStartDesc(
                            item.getId(),
                            BookingStatus.REJECTED,
                            LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond())));
            BookingForItemDto bookingAfter = BookingMapper.toBookingForItemDto(
                    bookingRepository.findFirstByItemIdEqualsAndStatusIsNotAndStartAfterOrderByStartAsc(
                            item.getId(),
                            BookingStatus.REJECTED,
                            LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond())));
            List<CommentDto> comments = CommentMapper.toCommentDto(commentRepository.findAllByItemIdEqualsOrderByCreatedDesc(item.getId()));
            itemsForOwner.add(ItemMapper.toItemCommentDto(item, bookingBefore, bookingAfter, comments));
        }
        log.info("Выгружен список item, принадлежащих user {} размером {} записей", owner, itemsForOwner.size());
        return itemsForOwner;
    }

    @Override
    public List<ItemDto> searchItems(String text, int from, int size) {
        if (text == null || text.isBlank()) {
            log.info("Текст запроса пуст. Выгружен список itemId по запросу: '{}' размером 0 записей", text);
            return Collections.emptyList();
        }
        Pageable pageParams = PageRequest.of(fromToPage(from, size), size, Sort.by(Sort.Direction.ASC, "id"));
        List<ItemDto> items = ItemMapper.toItemDto(
                itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text, pageParams));
        log.info("Выгружен список item по запросу: '{}' размером {} записей", text, items.size());
        return items;
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, long itemId, long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            log.info("Ошибка при создании комментария к itemId id " + itemId + ". Пользователь " + userId + " не найден.");
            throw new NotFoundException(String.format(
                    "Ошибка при создании комментария к itemId id " + itemId + ". Пользователь " + userId + " не найден."));
        }
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            log.info("Ошибка при создании комментария к itemId id " + itemId + ". Предмет c id " + userId + " не найден.");
            throw new NotFoundException(String.format(
                    "Ошибка при создании комментария к itemId id " + itemId + ". Предмет c id " + userId + " не найден."));
        }
        LocalDateTime dt = LocalDateTime.now();
        Booking booking = bookingRepository.findFirstByBookerIdEqualsAndItemIdEqualsAndEndBefore(
                userId,
                itemId,
                LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond()));
        if (booking == null) {
            log.info("Ошибка доступа. Пользователь, не бравший в аренду предмет, не может оставлять к нему комментарии.");
            throw new NoAccessException(
                    "Ошибка доступа. Пользователь, не бравший в аренду предмет, не может оставлять к нему комментарии.");
        }
        Comment newComment = CommentMapper.toComment(commentDto);
        newComment.setAuthor(user.get());
        newComment.setItem(item.get());
        newComment.setCreated(
                LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond()));
        Comment returnValue = commentRepository.save(newComment);
        log.info("Создан комментарий {}.", returnValue);
        return CommentMapper.toCommentDto(returnValue);
    }

    private int fromToPage(int from, int size) {
        if (from < 0 || size <= 0) {
            log.info("Переданы некорректные параметры from {} или size {}, проверьте правильность запроса.", from, size);
            throw new WrongInputDataException(String.format(
                    "Переданы некорректные параметры from %s или size %s, проверьте правильность запроса.", from, size));
        }
        float result = (float) from / size;
        return (int) Math.ceil(result);
    }
}
