package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongInputDataException;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    @Autowired
    private final ItemRequestRepository requestRepository;
    @Autowired
    private final ItemRepository itemRepository;
    @Autowired
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createRequest(ItemRequestDto request, long userId) {
        LocalDateTime dt = LocalDateTime.now();
        LocalDateTime dateTime = LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
        Optional<User> requester = userRepository.findById(userId);
        if (requester.isEmpty()) {
            log.info("Невозможно создать request, пользователь с id = " + userId + " не найден.");
            throw new NotFoundException("Невозможно создать request, пользователь с id = " + userId + " не найден.");
        }
        ItemRequest newRequest = ItemRequestMapper.toItemRequest(request, requester.get(), dateTime);
        ItemRequest returnValue = requestRepository.save(newRequest);
        log.info("Создан request: {}", returnValue);
        return ItemRequestMapper.toItemRequestDto(returnValue);
    }

    @Override
    public ItemRequestResponseDto getRequest(long userId, long requestId) {
        Optional<User> requestor = userRepository.findById(userId);
        if (requestor.isEmpty()) {
            log.info("Невозможно получить request, пользователь с id = " + userId + " не найден.");
            throw new NotFoundException("Невозможно получить request, пользователь с id = " + userId + " не найден.");
        }
        Optional<ItemRequest> request = requestRepository.findById(requestId);
        if (request.isEmpty()) {
            log.info("Request id " + requestId + " не найден.");
            throw new NotFoundException("Request id " + requestId + " не найден.");
        }
        List<ItemForResponseDto> items = itemRepository.findAllByRequestIdEquals(request.get().getId());
        ItemRequestResponseDto requestResponse = ItemRequestMapper.toItemRequestResponseDto(request.get(), items);
        log.info("Выгружен request id {}, для пользователя id {}.", requestResponse.getId(), userId);
        return requestResponse;
    }

    @Override
    public List<ItemRequestResponseDto> getRequests(long userId) {
        Optional<User> requestor = userRepository.findById(userId);
        if (requestor.isEmpty()) {
            log.info("Невозможно получить список request, пользователь с id = " + userId + " не найден.");
            throw new NotFoundException("Невозможно получить список request, пользователь с id = " + userId + " не найден.");
        }
        List<ItemRequest> requests = requestRepository.findByRequesterIdEqualsOrderByCreatedDesc(userId);
        List<ItemRequestResponseDto> requestResponseList = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<ItemForResponseDto> items = itemRepository.findAllByRequestIdEquals(request.getId());
            requestResponseList.add(ItemRequestMapper.toItemRequestResponseDto(request, items));
        }
        log.info("Выгружен список request, для пользователя id {} размером {} записей.", userId, requestResponseList.size());
        return requestResponseList;
    }

    @Override
    public List<ItemRequestResponseDto> searchRequests(long userId, int from, int size) {
        Optional<User> requestor = userRepository.findById(userId);
        if (requestor.isEmpty()) {
            log.info("Невозможно получить список request, пользователь с id = " + userId + " не найден.");
            throw new NotFoundException("Невозможно получить список request, пользователь с id = " + userId + " не найден.");
        }
        Pageable pageParams = PageRequest.of(fromToPage(from, size), size, Sort.by(Sort.Direction.DESC, "created"));
        Page<ItemRequest> requests = requestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageParams);
        List<ItemRequestResponseDto> requestResponseList = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<ItemForResponseDto> items = itemRepository.findAllByRequestIdEquals(request.getId());
            requestResponseList.add(ItemRequestMapper.toItemRequestResponseDto(request, items));
        }
        log.info("Выгружен список request, доступных для ответа пользователю id {} размером {} записей.",
                userId, requestResponseList.size());
        return requestResponseList;
    }

    private int fromToPage(int from, int size) {
        if (from < 0 || size <= 0) {
            log.info("Переданы некорректные параметры from {} или size {}, проверьте правильность запроса.", from, size);
            throw new WrongInputDataException(String.format(
                    "Переданы некорректные параметры from %s или size %s, проверьте правильность запроса.", from, size));
        }
        float result = (float) from / size;
        return (int) Math.ceil(result);
    }
}
