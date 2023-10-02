package ru.practicum.shareit.booking.validation;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

@Slf4j
public class BookingValidator {
    public static void validateBookingShortDto(BookingShortDto bookingDto) {
        LocalDateTime start = LocalDateTime.parse(bookingDto.getStart());
        LocalDateTime end = LocalDateTime.parse(bookingDto.getEnd());
        if (start.isAfter(end)
                || start.isBefore(LocalDateTime.now().minusSeconds(5))
                || end.isBefore(LocalDateTime.now().minusSeconds(5))
                || end.isEqual(start)) {
            log.info("Невозможно создать бронирование, некорректные даты начала и окончания.");
            throw new ValidationException("Невозможно создать бронирование, некорректные даты начала и окончания.");
        }
    }
}
