package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingShortDto {
    @NotNull(message = "Не задана дата и время начала бронирования.")
    private String start;
    @NotNull(message = "Не задана дата и время окончания бронирования.")
    private String end;
    private long itemId;
}
