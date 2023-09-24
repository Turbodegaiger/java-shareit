package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    BookingService bookingService;
    @MockBean
    static UserService userService;
    @MockBean
    static ItemService itemService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final ItemDto testItem1 = new ItemDto(1, "predmet", "prosto predmet", true, null, 1);
    private final UserDto testUser1 = new UserDto(1L, "user1", "user1@ya.ru");
    private final UserDto testUser2 = new UserDto(2L, "user2", "user2@ya.ru");
    private final BookingDto testBookingDto = new BookingDto(
            1,
            LocalDateTime.now().toString(),
            LocalDateTime.now().plusDays(1).toString(),
            testItem1,
            testUser1,
            BookingStatus.WAITING);
    private final BookingDto incorrectBookingDto = new BookingDto(
            1,
            null,
            LocalDateTime.now().plusDays(1).toString(),
            testItem1,
            testUser1,
            BookingStatus.APPROVED);

    void setUp() {
        userService.createUser(testUser1);
        userService.createUser(testUser2);
        itemService.createItem(testItem1, testUser2.getId());
    }

    @SneakyThrows
    @Test
    void createNewBookingTest_whenInvoke_thenResponseStatusIsCreatedAndReturnsNewBooking() {
        setUp();
        when(bookingService.createBooking(Mockito.any(), anyLong()))
                .thenReturn(testBookingDto);

        String result = mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(testBookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(testBookingDto), result);
    }

    @SneakyThrows
    @Test
    void approveOrDenyBookingTest_whenInvoked_returnApprovedBooking() {
        BookingDto approvedTestBooking = testBookingDto;
        approvedTestBooking.setStatus(BookingStatus.APPROVED);
        Boolean approved = true;
        when(bookingService.approveOrDenyBooking(testBookingDto.getId(), testUser1.getId(), approved))
                .thenReturn(testBookingDto);

        String result = mvc.perform(patch("/bookings/{bookingId}", testBookingDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .queryParam("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(approvedTestBooking), result);
    }

    @SneakyThrows
    @Test
    void getBookingTest_whenInvoked_returnBooking() {
        when(bookingService.getBooking(testBookingDto.getId(), testUser1.getId()))
                .thenReturn(testBookingDto);

        String result = mvc.perform(get("/bookings/{bookingId}", testBookingDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(testBookingDto), result);
    }

    @SneakyThrows
    @Test
    void getUserBookingsTest_whenInvoked_returnBookingList() {
        List<BookingDto> bookings = List.of(testBookingDto);
        when(bookingService.getAllUserBookings(testUser1.getId(), "ALL", 0, 10))
                .thenReturn(bookings);
        String result = mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookings), result);
    }

    @SneakyThrows
    @Test
    void getOwnerBookingsTest_whenInvoked_returnBookingList() {
        List<BookingDto> bookings = List.of(testBookingDto);
        when(bookingService.getAllOwnerBookings(testUser2.getId(), "ALL", 0, 10))
                .thenReturn(bookings);
        String result = mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(bookings), result);
    }
}
