package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.BookingStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingForItemDto {
    long id;
    String start;
    String end;
    long itemId;
    long bookerId;
    BookingStatus status;
}