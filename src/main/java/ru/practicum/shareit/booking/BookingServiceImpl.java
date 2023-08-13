package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.enums.BookingSearchState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.exception.WrongInputDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    @Autowired
    private final BookingRepository bookingRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final ItemRepository itemRepository;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingDto createBooking(BookingShortDto bookingDto, long userId) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            log.info("Невозможно создать бронирование без даты начала и даты окончания.");
            throw new ValidationException("Невозможно создать бронирование без даты начала и даты окончания.");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        if (booking.getStart().isAfter(booking.getEnd())
            || booking.getStart().isBefore(LocalDateTime.now())
            || booking.getEnd().isBefore(LocalDateTime.now())
            || booking.getEnd().isEqual(booking.getStart())) {
            log.info("Невозможно создать бронирование, некорректные даты начала и окончания.");
            throw new ValidationException("Невозможно создать бронирование, некорректные даты начала и окончания.");
        }
        Optional<User> booker = userRepository.findById(userId);
        if (booker.isEmpty()) {
            log.info("Невозможно создать бронирование, пользователь с id = " + userId + " не найден.");
            throw new NotFoundException("Невозможно создать бронирование, пользователь с id = " + userId + " не найден.");
        }
        booking.setBooker(booker.get());
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        if (item.isEmpty()) {
            log.info("Невозможно создать бронирование, предмет с id = " + bookingDto.getItemId() + " не найден.");
            throw new NotFoundException("Невозможно создать бронирование, предмет с id = " + bookingDto.getItemId() + " не найден.");
        }
        booking.setItem(item.get());
        if (!booking.getItem().getAvailable()) {
                log.info("Item {} уже забронирован на выбранные даты, бронирование недоступно.", booking.getItem().getId());
                throw new NoAccessException("Item " + booking.getItem().getId() + " уже забронирован на выбранные даты.");
        }
        if (booking.getItem().getOwner().getId() == booking.getBooker().getId()) {
            log.info("Пользователь {} является владельцем Item {}, нельзя создать бронирование.",
                    booking.getBooker().getId(), booking.getItem().getId());
            throw new NotFoundException(String.format("Пользователь %s является владельцем Item %s, нельзя создать бронирование.",
                    booking.getBooker().getId(), booking.getItem().getId()));
        }
        booking.getItem().setAvailable(false);
        booking.setStatus(BookingStatus.WAITING);
        Booking newBooking = bookingRepository.save(booking);
        log.info("Создано бронирование с id = {} на предмет id {}, автор = {}, владелец вещи = {}.", newBooking.getId(),
                newBooking.getItem().getId(), newBooking.getBooker().getId(), newBooking.getItem().getOwner().getId());
        return BookingMapper.toBookingDto(newBooking);
    }

    @Override
    public BookingDto approveOrDenyBooking(long bookingId, long userId, boolean approved) {
        Booking booking = findBookingById(bookingId);
        if (booking.getItem().getOwner().getId() == userId) {
            if (booking.getStatus() == BookingStatus.WAITING) {
                if (approved) {
                    booking.setStatus(BookingStatus.APPROVED);
                } else {
                    booking.setStatus(BookingStatus.REJECTED);
                }
                booking.getItem().setAvailable(true);
                itemRepository.save(booking.getItem());
                Booking updatedBooking = bookingRepository.save(booking);
                return BookingMapper.toBookingDto(updatedBooking);
            } else {
                log.info("У пользователя id = {} нет доступа к принятию/отклонению бронирования c id = {}.", userId, bookingId);
                throw new NoAccessException(String.format(
                        "У пользователя id = %s нет доступа к принятию/отклонению бронирования c id = %s.",
                        userId, bookingId));
            }
        }
        log.info("У пользователя id = {} нет доступных к принятию/отклонению бронирований c id = {}.", userId, bookingId);
        throw new NotFoundException(String.format(
                "У пользователя id = %s нет доступных к принятию/отклонению бронирований c id = %s.",
                userId, bookingId));
    }

    @Override
    public BookingDto getBooking(long bookingId, long userId) {
        Booking booking = findBookingById(bookingId);
        if (booking.getItem().getOwner().getId() == userId || booking.getBooker().getId() == userId) {
            log.info("Выгружен booking с id = {}, на предмет id {}, автор = {}, владелец вещи = {}.", booking.getId(),
                    booking.getItem().getId(), booking.getBooker().getId(), booking.getItem().getOwner().getId());
            return BookingMapper.toBookingDto(booking);
        }
        log.info("Для указанного пользователя id = {} не найдено бронирование c id = {}.", userId, bookingId);
        throw new NotFoundException(String.format(
                "Ошибка доступа, для указанного пользователя id = %s не найдено бронирование c id = %s.", userId, bookingId));
    }

    @Override
    public List<BookingDto> getAllUserBookings(long userId, String state) {
        BookingSearchState searchState;
        try {
            searchState = BookingSearchState.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new WrongInputDataException("Unknown state: " + state);
        }
        List<BookingDto> bookingList = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        switch (searchState) {
            case ALL:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdEqualsOrderByStartDesc(userId));
                break;
            case CURRENT:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdEqualsAndStartBeforeAndEndAfterOrderByStartDesc(userId, currentTime, currentTime));
                break;
            case PAST:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdEqualsAndEndBeforeOrderByStartDesc(userId, currentTime));
                break;
            case FUTURE:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdEqualsAndStartAfterOrderByStartDesc(userId, currentTime));
                break;
            case WAITING:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdEqualsAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING));
                break;
            case REJECTED:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdEqualsAndStatusEqualsOrderByStartDesc(userId, BookingStatus.REJECTED));
                break;
        }
        if (bookingList.isEmpty()) {
            log.info("Не найдено бронирований, соответствующих запросу.");
            throw new NotFoundException("Не найдено бронирований, соответствующих запросу.");
        }
        log.info("Выгружен список бронирований, оформленных пользователем id {} размером {} записей.", userId, bookingList.size());
        return bookingList;
    }

    @Override
    public List<BookingDto> getAllOwnerBookings(long userId, String state) {
        BookingSearchState searchState;
        try {
            searchState = BookingSearchState.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new WrongInputDataException("Unknown state: " + state);
        }
        List<BookingDto> bookingList = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        switch (searchState) {
            case ALL:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByItemOwnerIdEqualsOrderByStartDesc(userId));
                break;
            case CURRENT:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByItemOwnerIdEqualsAndStartBeforeAndEndAfterOrderByStartDesc(userId, currentTime, currentTime));
                break;
            case PAST:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByItemOwnerIdEqualsAndEndBeforeOrderByStartDesc(userId, currentTime));
                break;
            case FUTURE:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByItemOwnerIdEqualsAndStartAfterOrderByStartDesc(userId, currentTime));
                break;
            case WAITING:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByItemOwnerIdEqualsAndStatusEqualsOrderByStartDesc(userId, BookingStatus.WAITING));
                break;
            case REJECTED:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByItemOwnerIdEqualsAndStatusEqualsOrderByStartDesc(userId, BookingStatus.REJECTED));
                break;
        }
        if (bookingList.isEmpty()) {
            log.info("Не найдено бронирований, соответствующих запросу.");
            throw new NotFoundException("Не найдено бронирований, соответствующих запросу.");
        }
        log.info("Выгружен список бронирований, где пользователь id {} - владелец, размером {} записей.", userId, bookingList.size());
        return bookingList;
    }

    private Booking findBookingById(long bookingId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            log.info("Ошибка при выгрузке бронирования, booking с id = " + bookingId + " не найден.");
            throw new NotFoundException("Ошибка при выгрузке бронирования, booking с id = " + bookingId + " не найден.");
        }
        return booking.get();
    }
}
