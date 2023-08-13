package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemDao {
    private final HashMap<Long, Item> items = new HashMap<>();
    private static Long currentId = 0L;

    public Item createItem(@Valid Item item) {
        item.setId(generateId());
        items.put(item.getId(), item);
        log.info("Создан itemId {} с id = {}.", item.getName(), item.getId());
        return items.get(item.getId());
    }

    public Item updateItem(Item item, long itemId, long userId) {
        Item returnValue = null;
        if (items.containsKey(itemId) && items.get(itemId).getOwner().getId() == userId) {
            Item updatedItem = items.get(itemId);
            if (item.getName() != null && !item.getName().isBlank()) {
                updatedItem.setName(item.getName());
            }
            if (item.getDescription() != null && !item.getDescription().isBlank()) {
                updatedItem.setDescription(item.getDescription());
            }
            if (item.getAvailable() != null) {
                updatedItem.setAvailable(item.getAvailable());
            }
            items.replace(itemId, updatedItem);
            returnValue = items.get(itemId);
        }
        if (returnValue == null) {
            log.info("Не найден itemId с id = {}, у которого владелец - user {}.", itemId, userId);
            throw new NotFoundException(String.format("Не найден itemId с id = %s, у которого владелец - user %s.", itemId, userId));
        }
        log.info("Обновлён itemId с id = {}, владелец - {}.", itemId, userId);
        return returnValue;
    }

    public Item getItem(long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            log.info("Не найден itemId с id = {}", itemId);
            throw new NotFoundException(String.format("Не найден itemId с id = %s.", itemId));
        }
        log.info("Выгружен itemId с id = {}, владелец - user {}", itemId, item.getOwner());
        return item;
    }

    public List<Item> getItems(long userId) {
        List<Item> itemList = items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
        if (itemList.isEmpty()) {
            log.info("Не найдены itemId, принадлежащих user {}.", userId);
            throw new NotFoundException(String.format("Не найдены itemId, принадлежащих user %s.", userId));
        }
        log.info("Выгружен список itemId, принадлежащих user {} размером {} записей", userId, itemList.size());
        return itemList;
    }

    public List<Item> searchItems(String text) {
        List<Item> itemList = items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
        if (itemList.isEmpty()) {
            log.info("Не найдены itemId по запросу: '{}'.", text);
            throw new NotFoundException(String.format("Не найдены itemId по запросу: '%s'", text));
        }
        log.info("Выгружен список itemId по запросу: '{}' размером {} записей", text, itemList.size());
        return itemList;
    }

    private Long generateId() {
        return ++currentId;
    }
}
