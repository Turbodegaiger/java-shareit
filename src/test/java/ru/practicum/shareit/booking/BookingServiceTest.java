package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
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
    @InjectMocks
    UserServiceImpl userService;
    @InjectMocks
    ItemServiceImpl itemService;
    boolean loaded = false;

    private final ItemDto testItemDto1 = new ItemDto(1, "predmet", "prosto predmet", true, 0, 2);
    private final UserDto testUserDto1 = new UserDto(1L, "user1", "user1@ya.ru");
    private final UserDto testUserDto2 = new UserDto(2L, "user2", "user2@ya.ru");
    private final Booking testBooking1 = new Booking(
            1L,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            new Item(
                    1L,
                    "predmet",
                    "prosto predmet",
                    false,
                    0L,
                    UserMapper.mapToNewUser(testUserDto2)),
            UserMapper.mapToNewUser(testUserDto1),
            BookingStatus.WAITING);
    private Item testItem1 = new Item(
            1L,
            "predmet",
            "prosto predmet",
            true,
            0L,
            UserMapper.mapToNewUser(testUserDto2));

    private final User testUser1 = new User(
            1L,
            "user1",
            "user1@ya.ru");

    private static BookingShortDto testShortBooking1 = new BookingShortDto(
            LocalDateTime.now().toString(),
            LocalDateTime.now().plusDays(1).toString(),
            1);
    @BeforeEach
    void setUp() {
        if (!loaded) {
            when(userRepository.save(UserMapper.mapToNewUser(testUserDto1)))
                    .thenReturn(UserMapper.mapToNewUser(testUserDto1));
            when(userRepository.save(UserMapper.mapToNewUser(testUserDto2)))
                    .thenReturn(UserMapper.mapToNewUser(testUserDto2));
            when(itemRepository.save(testItem1))
                    .thenReturn(testItem1);
            when(userRepository.findById(testUserDto2.getId()))
                    .thenReturn(Optional.of(UserMapper.mapToNewUser(testUserDto2)));
            userService.createUser(testUserDto1);
            userService.createUser(testUserDto2);
            itemService.createItem(testItemDto1, testUserDto2.getId());
            loaded = true;
        }
    }

    @BeforeEach
    void refreshDateTime() {
        testShortBooking1.setStart(LocalDateTime.now().toString());
        testShortBooking1.setEnd(LocalDateTime.now().plusDays(1).toString());
        testBooking1.setStart(LocalDateTime.now());
        testBooking1.setEnd(LocalDateTime.now().plusDays(1));
    }

    @Test
    void createBooking_whenInvoke_returnNewBooking() {
        long userId = 1;
        BookingDto expectedTestBookingDto = new BookingDto(
                1,
                testShortBooking1.getStart(),
                testShortBooking1.getEnd(),
                testItemDto1,
                testUserDto1,
                BookingStatus.WAITING);
//
        Booking booking = testBooking1;
//        booking.setId(0L);
//
//        when(userRepository.findById(userId))
//                .thenReturn(Optional.of(testUser1));
//        when(itemRepository.findById(testShortBooking1.getItemId()))
//                .thenReturn(Optional.of(testItem1));
        when(bookingRepository.save(booking))
                .thenThrow(new RuntimeException());
        BookingDto result = bookingService.createBooking1(testShortBooking1, userId);
        assertEquals(expectedTestBookingDto, result);
    }

    @Test
    void createBooking_whenInvokeWithInvalidFields_thenValidationException() {
        long userId = 1;
        BookingShortDto incorrectBooking = testShortBooking1;
        incorrectBooking.setStart(null);
        assertThrows(ValidationException.class, () -> bookingService.createBooking(incorrectBooking, userId));
    }

    @Test
    void createBooking_whenThereIsNoSuchBooker_thenNotFoundException() {
        long userId = 3;
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(testShortBooking1, userId));
    }

    @Test
    void createBooking_whenThereIsNoSuchItem_thenNotFoundException() {
        long userId = 1;
        BookingShortDto incorrectBooking = testShortBooking1;
        incorrectBooking.setItemId(2);
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(incorrectBooking, userId));
    }

    @Test
    void createBooking_whenBookerIsOwner_thenNotFoundException() {
        long userId = 2;
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(testShortBooking1, userId));
    }
}
