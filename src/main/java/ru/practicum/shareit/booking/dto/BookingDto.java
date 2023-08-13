package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    long id;
    String start;
    String end;
    ItemDto item;
    UserDto booker;
    BookingStatus status;
}