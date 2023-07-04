package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto item);
    ItemDto updateItem(ItemDto item);
    ItemDto getItem(Long itemId);
    List<ItemDto> getItems();
    List<ItemDto> searchItems();
}
