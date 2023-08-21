package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(ItemRequestDto request, long userId);
    ItemRequestDto getRequest(long userId, long requestId);
    List<ItemRequestDto> getRequests(long userId);
    Page<ItemRequestDto> searchRequests(long userId, int from, int size);
}
