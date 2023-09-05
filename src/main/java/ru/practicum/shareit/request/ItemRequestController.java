package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-itemId-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final String userIdHeader = "X-Sharer-User-Id";
    @Autowired
    private final ItemRequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createRequest(@Valid @RequestBody ItemRequestDto request,
                              @RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на создание ItemRequest от пользователя {}.", userId);
        return requestService.createRequest(request, userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestResponseDto> getRequests(@RequestHeader(userIdHeader) long userId) {
        log.info("Принят запрос на получение запросов для пользователя {}.", userId);
        return requestService.getRequests(userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestResponseDto getRequest(@RequestHeader(userIdHeader) long userId, @PathVariable long requestId) {
        log.info("Принят запрос на получение запросов для пользователя {}.", userId);
        return requestService.getRequest(userId, requestId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestResponseDto> searchRequests(@RequestHeader(userIdHeader) long userId,
                                               @RequestParam(defaultValue = "0", required = false) int from,
                                               @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("Принят запрос от пользователя {} на поиск всех запросов.", userId);
        return requestService.searchRequests(userId, from, size);
    }
}
