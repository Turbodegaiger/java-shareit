package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUpdate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    private final ItemClient itemClient;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto item,
                                             @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на создание itemId.");
        return itemClient.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateItem(@RequestBody @Valid ItemForUpdate item,
                                             @PathVariable long itemId,
                                             @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на изменения itemId id = {}.", itemId);
        return itemClient.updateItem(item, itemId, userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItem(@PathVariable long itemId, @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на получение itemId id = {}.", itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getItems(@RequestHeader(userIdHeader) long userId,
                                           @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                           @Positive @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Принят запрос на получение всех itemId для пользователя {}.", userId);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> searchItems(@RequestHeader(userIdHeader) long userId,
                                              @RequestParam String text,
                                              @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                              @Positive @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Принят запрос на поиск itemId, где название или описание содержит '{}'", text);
        return itemClient.searchItems(text, from, size, userId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> createComment(@Valid @RequestBody CommentDto comment,
                                                @PathVariable long itemId,
                                                @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на пост Comment'а для itemId id {} от пользователя {}.", itemId, userId);
        return itemClient.createComment(comment, itemId, userId);
    }
}