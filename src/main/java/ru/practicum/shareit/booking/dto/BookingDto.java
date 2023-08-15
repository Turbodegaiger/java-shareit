package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private long id;
    @NotNull(message = "Не задана дата и время начала бронирования.")
    private String start;
    @NotNull(message = "Не задана дата и время окончания бронирования.")
    private String end;
    private ItemDto item;
    private UserDto booker;
    private BookingStatus status;
}