package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCommentDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private long ownerId;
    private BookingForItemDto lastBooking;
    private BookingForItemDto nextBooking;
    private List<CommentDto> comments;
}