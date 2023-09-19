package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        Optional<Item> item = itemRepository.findById(bookingDto.getItemId());
        Optional<User> booker = userRepository.findById(userId);
        if (item.isEmpty()) {
            log.info("Невозможно создать бронирование, предмет с id = " + bookingDto.getItemId() + " не найден.");
            throw new NotFoundException("Невозможно создать бронирование, предмет с id = " + bookingDto.getItemId() + " не найден.");
        }
        if (booker.isEmpty()) {
            log.info("Невозможно создать бронирование, пользователь с id = " + userId + " не найден.");
            throw new NotFoundException("Невозможно создать бронирование, пользователь с id = " + userId + " не найден.");
        }
        Booking booking = BookingMapper.toNewBooking(bookingDto, item.get(), booker.get());
        if (booking.getStart().isAfter(booking.getEnd())
                || booking.getStart().isBefore(LocalDateTime.now().minusSeconds(5))
                || booking.getEnd().isBefore(LocalDateTime.now().minusSeconds(5))
                || booking.getEnd().isEqual(booking.getStart())) {
            log.info("Невозможно создать бронирование, некорректные даты начала и окончания.");
            throw new ValidationException("Невозможно создать бронирование, некорректные даты начала и окончания.");
        }
        if (!booking.getItem().getAvailable()) {
                log.info("Item {} уже забронирован на выбранные даты, бронирование недоступно.", booking.getItem().getId());
                throw new NoAccessException("Item " + booking.getItem().getId() + " уже забронирован на выбранные даты.");
        }
        if (booking.getItem().getOwner().getId().equals(booking.getBooker().getId())) {
            log.info("Пользователь {} является владельцем Item {}, нельзя создать бронирование.",
                    booking.getBooker().getId(), booking.getItem().getId());
            throw new NotFoundException(String.format("Пользователь %s является владельцем Item %s, нельзя создать бронирование.",
                    booking.getBooker().getId(), booking.getItem().getId()));
        }
        booking.getItem().setAvailable(false);
        Booking newBooking = bookingRepository.save(booking);
        log.info("Создано бронирование {}.", newBooking);
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
            log.info("Выгружено бронирование {}.", booking);
            return BookingMapper.toBookingDto(booking);
        }
        log.info("Для указанного пользователя id = {} не найдено бронирование c id = {}.", userId, bookingId);
        throw new NotFoundException(String.format(
                "Ошибка доступа, для указанного пользователя id = %s не найдено бронирование c id = %s.", userId, bookingId));
    }

    @Override
    public List<BookingDto> getAllUserBookings(long userId, String state, int from, int size) {
        BookingSearchState searchState;
        try {
            searchState = BookingSearchState.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new WrongInputDataException("Unknown state: " + state);
        }
        Pageable pageParams = PageRequest.of(fromToPage(from, size), size, Sort.by(Sort.Direction.DESC, "start"));
        List<BookingDto> bookingList = new ArrayList<>();
        LocalDateTime dt = LocalDateTime.now();
        LocalDateTime dateTime = LocalDateTime.of(
                dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
        switch (searchState) {
            case ALL:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdEquals(userId, pageParams));
                break;
            case CURRENT:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdEqualsAndStartBeforeAndEndAfter(userId, dateTime, dateTime, pageParams));
                break;
            case PAST:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdEqualsAndEndBefore(userId, dateTime, pageParams));
                break;
            case FUTURE:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdEqualsAndStartAfter(userId, dateTime, pageParams));
                break;
            case WAITING:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdEqualsAndStatusEquals(userId, BookingStatus.WAITING, pageParams));
                break;
            case REJECTED:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByBookerIdEqualsAndStatusEquals(userId, BookingStatus.REJECTED, pageParams));
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
    public List<BookingDto> getAllOwnerBookings(long userId, String state, int from, int size) {
        BookingSearchState searchState;
        try {
            searchState = BookingSearchState.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new WrongInputDataException("Unknown state: " + state);
        }
        Pageable pageParams = PageRequest.of(fromToPage(from, size), size, Sort.by(Sort.Direction.DESC, "start"));
        List<BookingDto> bookingList = new ArrayList<>();
        LocalDateTime dt = LocalDateTime.now();
        LocalDateTime dateTime = LocalDateTime.of(
                dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
        switch (searchState) {
            case ALL:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByItemOwnerIdEquals(userId, pageParams));
                break;
            case CURRENT:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByItemOwnerIdEqualsAndStartBeforeAndEndAfter(userId, dateTime, dateTime, pageParams));
                break;
            case PAST:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByItemOwnerIdEqualsAndEndBefore(userId, dateTime, pageParams));
                break;
            case FUTURE:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByItemOwnerIdEqualsAndStartAfter(userId, dateTime, pageParams));
                break;
            case WAITING:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByItemOwnerIdEqualsAndStatusEquals(userId, BookingStatus.WAITING, pageParams));
                break;
            case REJECTED:
                bookingList = BookingMapper.toBookingDto(bookingRepository
                        .findAllByItemOwnerIdEqualsAndStatusEquals(userId, BookingStatus.REJECTED, pageParams));
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
