package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    private final ItemRequestClient requestClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRequest(@Valid @RequestBody ItemRequestDto request,
                                                @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на создание ItemRequest от пользователя {}.", userId);
        return requestClient.createRequest(request, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequests(@RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на получение запросов для пользователя {}.", userId);
        return requestClient.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRequest(@RequestHeader(userIdHeader) long userId, @PathVariable long requestId) {
        log.info("Принят запрос на получение запросов для пользователя {}.", userId);
        return requestClient.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> searchRequests(@RequestHeader(userIdHeader) long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0", required = false) int from,
                                                 @Positive @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Принят запрос от пользователя {} на поиск всех запросов.", userId);
        return requestClient.searchRequests(userId, from, size);
    }
}
