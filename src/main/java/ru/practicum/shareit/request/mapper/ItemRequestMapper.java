package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest request, List<ItemForResponseDto> items) {
        if (request == null) {
            return null;
        }
        return new ItemRequestResponseDto(
                request.getId(),
                request.getDescription(),
                request.getRequestorId(),
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
                request.getRequestorId(),
                request.getCreated().toString());
    }

    public static List<ItemRequestDto> toItemRequestDto(Iterable<ItemRequest> requests) {
        List<ItemRequestDto> requestDtoList = new ArrayList<>();
        for (ItemRequest request : requests) {
            requestDtoList.add(toItemRequestDto(request));
        }
        return requestDtoList;
    }

    public static ItemRequest toItemRequest(ItemRequestDto request) {
        if (request == null) {
            return null;
        }
        LocalDateTime created;
        if (request.getCreated() == null) {
            created = LocalDateTime.now();
        } else {
            created = LocalDateTime.parse(request.getCreated());
        }
        return new ItemRequest(
                request.getId(),
                request.getDescription(),
                request.getRequestorId(),
                created);
    }
}
