package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto createItem(ItemDto item, long userId);

    ItemDto updateItem(ItemDto item, long itemId, long userId);

    ItemDto getItem(long itemId);

    List<ItemDto> getItems(long userId);

    List<ItemDto> searchItems(String text);
}
