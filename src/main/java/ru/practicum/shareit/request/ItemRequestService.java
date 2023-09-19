package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(ItemRequestDto request, long userId);

    ItemRequestResponseDto getRequest(long userId, long requestId);

    List<ItemRequestResponseDto> getRequests(long userId);

    List<ItemRequestResponseDto> searchRequests(long userId, int from, int size);
}
