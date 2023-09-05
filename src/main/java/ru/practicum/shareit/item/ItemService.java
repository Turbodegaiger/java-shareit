package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto item, long userId);

    ItemDto updateItem(ItemDto item, long itemId, long userId);

    ItemCommentDto getItem(long itemId, long userId);

    List<ItemCommentDto> getItems(long owner, int from, int size);

    List<ItemDto> searchItems(String text, int from, int size);

    CommentDto createComment(CommentDto commentDto, long itemId, long userId);
}
