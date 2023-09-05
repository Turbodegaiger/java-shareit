package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingShortDto bookingDto, long userId);

    BookingDto approveOrDenyBooking(long bookingId, long userId, boolean approved);

    BookingDto getBooking(long bookingId, long userId);

    List<BookingDto> getAllUserBookings(long userId, String state, int from, int size);

    List<BookingDto> getAllOwnerBookings(long userId, String state, int from, int size);
}
