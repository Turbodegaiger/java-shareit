package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    private final ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createItem(@Valid @RequestBody ItemDto item,
                              @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на создание itemId.");
        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto updateItem(@RequestBody ItemDto item,
                              @PathVariable long itemId,
                              @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на изменения itemId id = {}.", itemId);
        return itemService.updateItem(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemCommentDto getItem(@PathVariable long itemId, @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на получение itemId id = {}.", itemId);
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemCommentDto> getItems(@RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на получение всех itemId для пользователя {}.", userId);
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Принят запрос на поиск itemId, где название или описание содержит '{}'", text);
        return itemService.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto createComment(@Valid @RequestBody CommentDto comment,
                                    @PathVariable long itemId,
                                    @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на пост Comment'а для itemId id {} от пользователя {}.", itemId, userId);
        return itemService.createComment(comment, itemId, userId);
    }
}
