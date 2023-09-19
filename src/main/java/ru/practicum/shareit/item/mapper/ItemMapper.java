package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUpdate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        Long requestId = null;
        if (item.getRequest() != null) {
            requestId = item.getRequest().getId();
        }
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                requestId,
                item.getOwner().getId());
    }

    public static ItemCommentDto toItemCommentDto(Item item, BookingForItemDto bookingBeforeNow, BookingForItemDto bookingAfterNow, List<CommentDto> comment) {
        return new ItemCommentDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                bookingBeforeNow,
                bookingAfterNow,
                comment);
    }

    public static List<ItemDto> toItemDto(Iterable<Item> items) {
        List<ItemDto> requestDtoList = new ArrayList<>();
        for (Item item : items) {
            requestDtoList.add(toItemDto(item));
        }
        return requestDtoList;
    }

    public static Item toItem(ItemDto itemDto, User owner, boolean available, ItemRequest request) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                available,
                request,
                owner);
    }

    public static Item toItem(ItemForUpdate item, long itemId, User owner, ItemRequest request) {
        return new Item(
                itemId,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                request,
                owner);
    }
}
