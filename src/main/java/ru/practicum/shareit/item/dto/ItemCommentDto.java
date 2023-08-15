package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCommentDto {
    private long id;
    @NotEmpty(message = "Имя не может быть null")
    @Size(min = 1, max = 255)
    private String name;
    @NotEmpty(message = "Описание не может быть null")
    @Size(min = 1, max = 512)
    private String description;
    @NotNull(message = "Поле available не может быть null")
    private Boolean available;
    private long ownerId;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentDto> comments;
}