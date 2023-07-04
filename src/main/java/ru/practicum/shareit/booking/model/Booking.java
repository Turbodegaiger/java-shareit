package ru.practicum.shareit.booking.model;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    long item;
    long booker;
    BookingStatus status;

    public Booking(long id, LocalDateTime start, LocalDateTime end, long item, long booker, BookingStatus status) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.item = item;
        this.booker = booker;
        this.status = status;
    }
}
