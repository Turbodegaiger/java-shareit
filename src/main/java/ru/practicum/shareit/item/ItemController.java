package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    private ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@Valid @RequestBody ItemDto item,
                              @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на создание item.");
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestBody ItemDto item,
                              @PathVariable long itemId,
                              @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на изменения item id = {}.", itemId);
        return itemService.updateItem(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@PathVariable long itemId) {
        log.info("Принят запрос на получение item id = {}.", itemId);
        return itemService.getItem(itemId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItems(@RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на получение всех item.");
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Принят запрос на поиск item, где название или описание содержит '{}'", text);
        return itemService.searchItems(text);
    }
}
