package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private UserDao userDao;

    @Override
    public ItemDto createItem(ItemDto item, long userId) {
        Item newItem = ItemMapper.toItem(item, userId);
        userDao.getUser(newItem.getOwner());
        if (newItem.getName() == null || newItem.getName().isBlank()) {
            log.info("Невозможно создать item. Отсутствует имя или владелец.");
            throw new ValidationException("Невозможно создать item. Отсутствует имя или владелец.");
        }
        Item returnValue = itemDao.createItem(newItem);
        return ItemMapper.toItemDto(returnValue);
    }

    @Override
    public ItemDto updateItem(ItemDto item, long itemId, long userId) {
        Item updatedItem = itemDao.updateItem(ItemMapper.toItem(item, userId), itemId, userId);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    public ItemDto getItem(long itemId) {
        return ItemMapper.toItemDto(itemDao.getItem(itemId));
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemDao.getItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) {
            log.info("Текст запроса пуст. Выгружен список item по запросу: '{}' размером 0 записей", text);
            return Collections.emptyList();
        }
        return itemDao.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
