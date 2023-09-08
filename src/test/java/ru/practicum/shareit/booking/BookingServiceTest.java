package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongInputDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    Pageable pageParams = PageRequest.of(
            fromToPage(0, 10), 10, Sort.by(Sort.Direction.DESC, "start"));
    private final ItemDto testItemDto1 = new ItemDto(1, "predmet", "prosto predmet", true, 0, 2);
    private final UserDto testUserDto1 = new UserDto(1L, "user1", "user1@ya.ru");
    User testUser1 = new User(1L, "user1", "user1@ya.ru");
    User testUser2 = new User(2L, "user2", "user2@ya.ru");
    LocalDateTime dt = LocalDateTime.now();
    String dateTimeStart = LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond()).toString();
    String dateTimeEnd = LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond()).plusDays(1).toString();
    private Item testItem1 = new Item(
            1L,
            "predmet",
            "prosto predmet",
            true,
            0L,
            testUser2);
    private final Booking testBooking1 = new Booking(
            1L,
            LocalDateTime.parse(dateTimeStart),
            LocalDateTime.parse(dateTimeEnd),
            testItem1,
            testUser2,
            BookingStatus.WAITING);
    private BookingShortDto testShortBooking1 = new BookingShortDto(
            dateTimeStart,
            dateTimeEnd,
            1);

    @Test
    void createBookingTest_whenInvoke_returnNewBooking() {
        long userId = 1;
        long itemId = 1;
        BookingDto expectedTestBookingDto = new BookingDto(
                0,
                dateTimeStart,
                dateTimeEnd,
                testItemDto1,
                testUserDto1,
                BookingStatus.WAITING);
        expectedTestBookingDto.getItem().setAvailable(false);
        Booking booking = BookingMapper.toBooking(testShortBooking1);
        booking.setBooker(testUser1);
        booking.setItem(testItem1);
        booking.setStatus(BookingStatus.WAITING);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem1));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        BookingDto result = bookingService.createBooking(testShortBooking1, userId);

        assertEquals(expectedTestBookingDto, result);
    }

    @Test
    void createBookingTest_whenThereIsNoSuchBooker_thenNotFoundException() {
        long userId = 3;
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(testShortBooking1, userId));
    }

    @Test
    void createBookingTest_whenThereIsNoSuchItem_thenNotFoundException() {
        long userId = 1;
        BookingShortDto incorrectBooking = testShortBooking1;
        incorrectBooking.setItemId(2);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(incorrectBooking, userId));
    }

    @Test
    void createBookingTest_whenBookerIsOwner_thenNotFoundException() {
        long userId = 2;
        long itemId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser2));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItem1));
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(testShortBooking1, userId));
    }

    @Test
    void createBookingTest_whenItemNotAvailable_thenNoAccessException() {
        long userId = 1;
        long itemId = 1;
        Item testItemNotAvailable = testItem1;
        testItemNotAvailable.setAvailable(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser1));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(testItemNotAvailable));
        assertThrows(NoAccessException.class, () -> bookingService.createBooking(testShortBooking1, userId));
    }

    @Test
    void approveOrDenyBookingTest_whenOk_returnApprovedBookingDto() {
        Booking testBooking1Approved = new Booking(
                1L,
                LocalDateTime.parse(dateTimeStart),
                LocalDateTime.parse(dateTimeEnd),
                testItem1,
                testUser2,
                BookingStatus.WAITING);
        testBooking1Approved.getItem().setAvailable(true);
        testBooking1Approved.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking1));
        when(bookingRepository.save(testBooking1Approved)).thenReturn(testBooking1Approved);

        BookingDto result = bookingService.approveOrDenyBooking(1, 2, true);

        assertEquals(BookingMapper.toBookingDto(testBooking1Approved), result);
    }

    @Test
    void approveOrDenyBookingTest_whenOk_returnRejectedBookingDto() {
        Booking testBooking1Approved = new Booking(
                1L,
                LocalDateTime.parse(dateTimeStart),
                LocalDateTime.parse(dateTimeEnd),
                testItem1,
                testUser2,
                BookingStatus.WAITING);
        testBooking1Approved.getItem().setAvailable(true);
        testBooking1Approved.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking1));
        when(bookingRepository.save(testBooking1Approved)).thenReturn(testBooking1Approved);

        BookingDto result = bookingService.approveOrDenyBooking(1, 2, false);

        assertEquals(BookingMapper.toBookingDto(testBooking1Approved), result);
    }

    @Test
    void approveOrDenyBookingTest_whenUserIsNotOwner_NotFoundException() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking1));

        assertThrows(NotFoundException.class, () -> bookingService.approveOrDenyBooking(1, 1, true));
    }

    @Test
    void approveOrDenyBookingTest_whenNoSuchBooking_NotFoundException() {
        when(bookingRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approveOrDenyBooking(0, 2, true));
    }

    @Test
    void approveOrDenyBookingTest_whenAlreadyApproved_NoAccessException() {
        Booking testBooking1Approved = new Booking(
                1L,
                LocalDateTime.parse(dateTimeStart),
                LocalDateTime.parse(dateTimeEnd),
                testItem1,
                testUser2,
                BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking1Approved));

        assertThrows(NoAccessException.class, () -> bookingService.approveOrDenyBooking(1, 2, true));
    }

    @Test
    void getBookingTest_whenOk_returnBookingDto() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking1));

        BookingDto result = bookingService.getBooking(1, 2);

        assertEquals(BookingMapper.toBookingDto(testBooking1), result);
    }

    @Test
    void getBookingTest_whenNoSuchBookingForUser_NotFoundException() {
        Booking testBooking2 = new Booking(
                1L,
                LocalDateTime.parse(dateTimeStart),
                LocalDateTime.parse(dateTimeEnd),
                testItem1,
                testUser2,
                BookingStatus.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(testBooking2));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(1, 3));
    }

    @Test
    void getAllOwnerBookingsTest_whenOk_returnBookingDtoList() {
        int userId = 1;
        when(bookingRepository.findAllByItemOwnerIdEquals(userId, pageParams)).thenReturn(new PageImpl<>(List.of(testBooking1)));

        List<BookingDto> result = bookingService.getAllOwnerBookings(userId, "ALL", 0, 10);

        assertEquals(List.of(BookingMapper.toBookingDto(testBooking1)), result);
    }

    @Test
    void getAllOwnerBookingsTest_whenSearchOfCurrent_returnBookingDtoList() {
        int userId = 2;
        Booking testBooking2 = new Booking(
                2L,
                LocalDateTime.parse(dateTimeStart).minusDays(1),
                LocalDateTime.parse(dateTimeEnd),
                testItem1,
                testUser2,
                BookingStatus.WAITING);
        LocalDateTime currentTime = LocalDateTime.of(
                dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
        when(bookingRepository
                .findAllByItemOwnerIdEqualsAndStartBeforeAndEndAfter(userId, currentTime, currentTime, pageParams))
                .thenReturn(new PageImpl<>(List.of(testBooking2)));

        List<BookingDto> result = bookingService.getAllOwnerBookings(userId, "CURRENT", 0, 10);

        assertEquals(List.of(BookingMapper.toBookingDto(testBooking2)), result);
    }

    @Test
    void getAllOwnerBookingsTest_whenSearchOfPast_returnBookingDtoList() {
        int userId = 1;
        Booking testBooking2 = new Booking(
                2L,
                LocalDateTime.parse(dateTimeStart).minusDays(2),
                LocalDateTime.parse(dateTimeEnd).minusDays(1),
                testItem1,
                testUser2,
                BookingStatus.WAITING);
        LocalDateTime currentTime = LocalDateTime.of(
                dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());

        when(bookingRepository
                .findAllByItemOwnerIdEqualsAndEndBefore(userId, currentTime, pageParams))
                .thenReturn(new PageImpl<>(List.of(testBooking2)));

        List<BookingDto> result = bookingService.getAllOwnerBookings(userId, "PAST", 0, 10);

        assertEquals(List.of(BookingMapper.toBookingDto(testBooking2)), result);
    }

    @Test
    void getAllOwnerBookingsTest_whenSearchOfFuture_returnBookingDtoList() {
        int userId = 1;
        Booking testBooking2 = new Booking(
                2L,
                LocalDateTime.parse(dateTimeStart).plusDays(1),
                LocalDateTime.parse(dateTimeEnd).plusDays(2),
                testItem1,
                testUser2,
                BookingStatus.WAITING);
        LocalDateTime currentTime = LocalDateTime.of(
                dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
        when(bookingRepository
                .findAllByItemOwnerIdEqualsAndStartAfter(userId, currentTime, pageParams))
                .thenReturn(new PageImpl<>(List.of(testBooking2)));

        List<BookingDto> result = bookingService.getAllOwnerBookings(userId, "FUTURE", 0, 10);

        assertEquals(List.of(BookingMapper.toBookingDto(testBooking2)), result);
    }

    @Test
    void getAllOwnerBookingsTest_whenSearchOfWaiting_returnBookingDtoList() {
        int userId = 1;
        Booking testBooking2 = new Booking(
                2L,
                LocalDateTime.parse(dateTimeStart).minusDays(1),
                LocalDateTime.parse(dateTimeEnd),
                testItem1,
                testUser2,
                BookingStatus.WAITING);
        when(bookingRepository
                .findAllByItemOwnerIdEqualsAndStatusEquals(userId, BookingStatus.WAITING, pageParams))
                .thenReturn(new PageImpl<>(List.of(testBooking2)));

        List<BookingDto> result = bookingService.getAllOwnerBookings(userId, "WAITING", 0, 10);

        assertEquals(List.of(BookingMapper.toBookingDto(testBooking2)), result);
    }

    @Test
    void getAllOwnerBookingsTest_whenSearchOfRejected_returnBookingDtoList() {
        int userId = 1;
        Booking testBooking2 = new Booking(
                2L,
                LocalDateTime.parse(dateTimeStart).minusDays(1),
                LocalDateTime.parse(dateTimeEnd),
                testItem1,
                testUser2,
                BookingStatus.REJECTED);
        when(bookingRepository
                .findAllByItemOwnerIdEqualsAndStatusEquals(userId, BookingStatus.REJECTED, pageParams))
                .thenReturn(new PageImpl<>(List.of(testBooking2)));

        List<BookingDto> result = bookingService.getAllOwnerBookings(userId, "REJECTED", 0, 10);

        assertEquals(List.of(BookingMapper.toBookingDto(testBooking2)), result);
    }

    @Test
    void getAllOwnerBookingsTest_whenStateIsWrong_WrongArgumentException() {
        assertThrows(WrongInputDataException.class, () -> bookingService.getAllOwnerBookings(1, "WRONG_ARGUMENT", 0, 10));
    }

    @Test
    void getAllOwnerBookingsTest_whenBookingsNotFound_NotFoundException() {
        int userId = 3;
        when(bookingRepository
                .findAllByItemOwnerIdEqualsAndStatusEquals(userId, BookingStatus.REJECTED, pageParams))
                .thenReturn(new PageImpl<>(List.of()));

        assertThrows(NotFoundException.class, () -> bookingService.getAllOwnerBookings(3, "REJECTED", 0, 10));
    }

    @Test
    void getAllUserBookingsTest_whenInvalidPageParamsFrom_WrongInputDataException() {
        assertThrows(WrongInputDataException.class, () -> bookingService.getAllUserBookings(1, "REJECTED", -1, 10));
    }

    @Test
    void getAllUserBookingsTest_whenInvalidPageParamsSize_WrongInputDataException() {
        assertThrows(WrongInputDataException.class, () -> bookingService.getAllUserBookings(1, "REJECTED", 0, 0));
    }

    @Test
    void getAllUserBookingsTest_whenOk_returnBookingDtoList() {
        int userId = 1;
        when(bookingRepository.findAllByBookerIdEquals(userId, pageParams)).thenReturn(new PageImpl<>(List.of(testBooking1)));

        List<BookingDto> result = bookingService.getAllUserBookings(1, "ALL", 0, 10);

        assertEquals(List.of(BookingMapper.toBookingDto(testBooking1)), result);
    }

    @Test
    void getAllUserBookingsTest_whenSearchOfCurrent_returnBookingDtoList() {
        int userId = 1;
        Booking testBooking2 = new Booking(
                2L,
                LocalDateTime.parse(dateTimeStart).minusDays(1),
                LocalDateTime.parse(dateTimeEnd),
                testItem1,
                testUser2,
                BookingStatus.WAITING);
        LocalDateTime currentTime = LocalDateTime.of(
                dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
        when(bookingRepository
                .findAllByBookerIdEqualsAndStartBeforeAndEndAfter(userId, currentTime, currentTime, pageParams))
                .thenReturn(new PageImpl<>(List.of(testBooking2)));

        List<BookingDto> result = bookingService.getAllUserBookings(1, "CURRENT", 0, 10);

        assertEquals(List.of(BookingMapper.toBookingDto(testBooking2)), result);
    }

    @Test
    void getAllUserBookingsTest_whenSearchOfPast_returnBookingDtoList() {
        int userId = 1;
        Booking testBooking2 = new Booking(
                2L,
                LocalDateTime.parse(dateTimeStart).minusDays(2),
                LocalDateTime.parse(dateTimeEnd).minusDays(1),
                testItem1,
                testUser2,
                BookingStatus.WAITING);
        LocalDateTime currentTime = LocalDateTime.of(
                dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());

        when(bookingRepository
                .findAllByBookerIdEqualsAndEndBefore(userId, currentTime, pageParams))
                .thenReturn(new PageImpl<>(List.of(testBooking2)));

        List<BookingDto> result = bookingService.getAllUserBookings(1, "PAST", 0, 10);

        assertEquals(List.of(BookingMapper.toBookingDto(testBooking2)), result);
    }

    @Test
    void getAllUserBookingsTest_whenSearchOfFuture_returnBookingDtoList() {
        int userId = 1;
        Booking testBooking2 = new Booking(
                2L,
                LocalDateTime.parse(dateTimeStart).plusDays(1),
                LocalDateTime.parse(dateTimeEnd).plusDays(2),
                testItem1,
                testUser2,
                BookingStatus.WAITING);
        LocalDateTime currentTime = LocalDateTime.of(
                dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
        when(bookingRepository
                .findAllByBookerIdEqualsAndStartAfter(userId, currentTime, pageParams))
                .thenReturn(new PageImpl<>(List.of(testBooking2)));

        List<BookingDto> result = bookingService.getAllUserBookings(1, "FUTURE", 0, 10);

        assertEquals(List.of(BookingMapper.toBookingDto(testBooking2)), result);
    }

    @Test
    void getAllUserBookingsTest_whenSearchOfWaiting_returnBookingDtoList() {
        int userId = 1;
        Booking testBooking2 = new Booking(
                2L,
                LocalDateTime.parse(dateTimeStart).minusDays(1),
                LocalDateTime.parse(dateTimeEnd),
                testItem1,
                testUser2,
                BookingStatus.WAITING);
        when(bookingRepository
                .findAllByBookerIdEqualsAndStatusEquals(userId, BookingStatus.WAITING, pageParams))
                .thenReturn(new PageImpl<>(List.of(testBooking2)));

        List<BookingDto> result = bookingService.getAllUserBookings(1, "WAITING", 0, 10);

        assertEquals(List.of(BookingMapper.toBookingDto(testBooking2)), result);
    }

    @Test
    void getAllUserBookingsTest_whenSearchOfRejected_returnBookingDtoList() {
        int userId = 1;
        Booking testBooking2 = new Booking(
                2L,
                LocalDateTime.parse(dateTimeStart).minusDays(1),
                LocalDateTime.parse(dateTimeEnd),
                testItem1,
                testUser2,
                BookingStatus.REJECTED);
        when(bookingRepository
                .findAllByBookerIdEqualsAndStatusEquals(userId, BookingStatus.REJECTED, pageParams))
                .thenReturn(new PageImpl<>(List.of(testBooking2)));

        List<BookingDto> result = bookingService.getAllUserBookings(1, "REJECTED", 0, 10);

        assertEquals(List.of(BookingMapper.toBookingDto(testBooking2)), result);
    }

    @Test
    void getAllUserBookingsTest_whenStateIsWrong_WrongArgumentException() {
        assertThrows(WrongInputDataException.class, () -> bookingService.getAllUserBookings(1, "WRONG_ARGUMENT", 0, 10));
    }

    @Test
    void getAllUserBookingsTest_whenBookingsNotFound_NotFoundException() {
        int userId = 1;
        when(bookingRepository
                .findAllByBookerIdEqualsAndStatusEquals(userId, BookingStatus.REJECTED, pageParams))
                .thenReturn(new PageImpl<>(List.of()));

        assertThrows(NotFoundException.class, () -> bookingService.getAllUserBookings(1, "REJECTED", 0, 10));
    }

    private int fromToPage(int from, int size) {
        float result = (float) from / size;
        return (int) Math.ceil(result);
    }
}
