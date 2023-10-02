package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.enums.BookingStatus;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingForItemDto {
    private long id;
    @NotNull(message = "Не задана дата и время начала бронирования.")
    private String start;
    @NotNull(message = "Не задана дата и время окончания бронирования.")
    private String end;
    private long itemId;
    private long bookerId;
    private BookingStatus status;
}