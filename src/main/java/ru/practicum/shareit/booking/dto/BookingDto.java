package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDto {
    long id;
    LocalDateTime start;
    LocalDateTime end;
    long item;
    long booker;
    BookingStatus status;
}