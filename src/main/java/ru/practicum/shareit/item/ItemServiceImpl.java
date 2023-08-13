package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private final UserRepository userRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item newItem = ItemMapper.toItem(itemDto);
        if (newItem.getName() == null || newItem.getName().isBlank()) {
            log.info("Невозможно создать item. Отсутствует название.");
            throw new ValidationException("Невозможно создать item. Отсутствует название.");
        }
        User owner = userRepository.findById(userId).orElseThrow((
                ()-> new NotFoundException("Невозможно создать предмет, владелец с id = " + userId + " не найден.")));
        newItem.setOwner(owner);
        newItem.setAvailable(true);
        Item item = itemRepository.save(newItem);
        log.info("Создан itemId {} с id = {}, владелец - {}.", item.getName(), item.getId(), item.getOwner());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto item, long itemId, long userId) {
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
        Item newItem = ItemMapper.toItem(item);
        newItem.setId(itemId);
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
        log.info("Обновлён itemId с id = {}, владелец - {}.", itemId, owner.get().getId());
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemCommentDto getItem(long itemId, long userId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            log.info("Не найден itemId с id = {}.", itemId);
            throw new NotFoundException(String.format("Не найден itemId с id = %s.", itemId));
        }
        BookingForItemDto bookingBefore = null;
        BookingForItemDto bookingAfter = null;
        if (item.get().getOwner().getId() == userId) {
            bookingBefore = BookingMapper.toBookingForItemDto(
                    bookingRepository.findFirstByItemIdEqualsAndStatusIsNotAndStartBeforeOrderByStartDesc(
                            itemId, BookingStatus.REJECTED, LocalDateTime.now()));
            bookingAfter = BookingMapper.toBookingForItemDto(
                    bookingRepository.findFirstByItemIdEqualsAndStatusIsNotAndStartAfterOrderByStartAsc(
                            itemId, BookingStatus.REJECTED, LocalDateTime.now()));
        }
        List<Comment> comments = commentRepository.findAllByItemIdEqualsOrderByCreatedDesc(itemId);
        log.info("Выгружен itemId с id = {}, владелец - {}.", item.get().getId(), item.get().getOwner());
        return ItemMapper.toItemCommentDto(item.get(), bookingBefore, bookingAfter, comments);
    }

    @Override
    public List<ItemCommentDto> getItems(long owner) {
        List<Item> items = itemRepository.findAllByOwnerIdIsOrderByIdAsc(owner);
        List<ItemCommentDto> itemsForOwner = new ArrayList<>();
        for (Item item : items) {
            BookingForItemDto bookingBefore = BookingMapper.toBookingForItemDto(
                    bookingRepository.findFirstByItemIdEqualsAndStatusIsNotAndStartBeforeOrderByStartDesc(
                            item.getId(), BookingStatus.REJECTED, LocalDateTime.now()));
            BookingForItemDto bookingAfter = BookingMapper.toBookingForItemDto(
                    bookingRepository.findFirstByItemIdEqualsAndStatusIsNotAndStartAfterOrderByStartAsc(
                            item.getId(), BookingStatus.REJECTED, LocalDateTime.now()));
            List<Comment> comments = commentRepository.findAllByItemIdEqualsOrderByCreatedDesc(item.getId());
            itemsForOwner.add(ItemMapper.toItemCommentDto(item, bookingBefore, bookingAfter, comments));
        }
        log.info("Выгружен список itemId, принадлежащих user {} размером {} записей", owner, items.size());
        return itemsForOwner;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            log.info("Текст запроса пуст. Выгружен список itemId по запросу: '{}' размером 0 записей", text);
            return Collections.emptyList();
        }
        List<ItemDto> items = itemRepository.findAllByNameOrDescriptionContainingIgnoreCase(text, text).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Выгружен список itemId по запросу: '{}' размером {} записей", text, items.size());
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
        Booking booking = bookingRepository.findFirstByBookerIdEqualsAndItemIdEqualsAndEndBefore(userId, itemId, LocalDateTime.now());
        if (booking == null) {
            log.info("Ошибка доступа. Пользователь, не бравший в аренду предмет, не может оставлять к нему комментарии.");
            throw new NoAccessException(
                    "Ошибка доступа. Пользователь, не бравший в аренду предмет, не может оставлять к нему комментарии.");
        }
        if (commentDto.getText().isEmpty()) {
            log.info("Невозможно оставить пустой комментарий к item {} пользователем {}.", itemId, userId);
            throw new ValidationException(String.format(
                    "Невозможно оставить пустой комментарий к item %s пользователем %s.", itemId, userId));
        }
        Comment newComment = CommentMapper.toComment(commentDto);
        newComment.setAuthor(user.get());
        newComment.setItem(item.get());
        Comment returnValue = commentRepository.save(newComment);
        return CommentMapper.toCommentDto(returnValue);
    }
}
