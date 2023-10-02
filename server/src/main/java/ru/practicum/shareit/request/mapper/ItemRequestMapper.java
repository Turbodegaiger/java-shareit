package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest request, List<ItemForResponseDto> items) {
        if (request == null) {
            return null;
        }
        return new ItemRequestResponseDto(
                request.getId(),
                request.getDescription(),
                request.getRequester().getId(),
                request.getCreated().toString(),
                items);
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        if (request == null) {
            return null;
        }
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequester().getId(),
                request.getCreated().toString());
    }

    public static ItemRequest toItemRequest(ItemRequestDto request, User requester, LocalDateTime dateTime) {
        if (request == null) {
            return null;
        }
        return new ItemRequest(
                request.getId(),
                request.getDescription(),
                requester,
                dateTime);
    }
}
