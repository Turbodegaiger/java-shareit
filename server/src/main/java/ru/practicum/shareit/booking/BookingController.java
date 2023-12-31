package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    @Autowired
    private final BookingService bookingService;
    private final String userIdHeader = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto createBooking(@RequestBody BookingShortDto bookingDto,
                                    @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на создание booking.");
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto approveOrDenyBooking(@PathVariable long bookingId,
                                           @RequestParam boolean approved,
                                           @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на подтверждение бронирования id {}.", bookingId);
        return bookingService.approveOrDenyBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBooking(@PathVariable long bookingId,
                                 @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на получение booking id = {}.", bookingId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getUserBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                            @RequestHeader(userIdHeader) long userId,
                                            @RequestParam(defaultValue = "0", required = false) int from,
                                            @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Принят запрос на получение бронирований для пользователя id = {}.", userId);
        return bookingService.getAllUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getOwnerBookings(@RequestParam(required = false, defaultValue = "ALL") String state,
                                             @RequestHeader(userIdHeader) long userId,
                                             @RequestParam(defaultValue = "0", required = false) int from,
                                             @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Принят запрос на получение бронирований для пользователя-владельца id = {}.", userId);
        return bookingService.getAllOwnerBookings(userId, state, from, size);
    }
}
