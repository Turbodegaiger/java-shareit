package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @InjectMocks
    ItemRequestServiceImpl requestService;
    @Mock
    private ItemRequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    LocalDateTime dt = LocalDateTime.now();
    LocalDateTime dateTime = LocalDateTime.of(dt.getYear(), dt.getMonth(), dt.getDayOfMonth(), dt.getHour(), dt.getMinute(), dt.getSecond());
    ItemForResponseDto itemForResponseDto = new ItemForResponseDto(1, "predmet", "prosto predmet", true, null);
    ItemRequest testItemRequest1 = new ItemRequest(1, "nuzhen predmet", 1, dateTime);
    ItemRequestDto testItemRequestDto1 = ItemRequestMapper.toItemRequestDto(testItemRequest1);
    ItemRequestResponseDto testItemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(testItemRequest1, List.of(itemForResponseDto));
    UserDto testUserDto1 = new UserDto(1, "user1", "user1@ya.ru");
    ItemDto testItemDto1 = new ItemDto(1, "predmet", "prosto predmet", true, null, 2);
    User testUser1 = new User(1L, "user1", "user1@ya.ru");
    User testUser2 = new User(2L, "user2", "user2@ya.ru");

    @Test
    void createRequestTest_ifValid_returnItemRequestDto() {
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(testUser1));
        when(requestRepository.save(testItemRequest1)).thenReturn(testItemRequest1);

        ItemRequestDto result = requestService.createRequest(testItemRequestDto1, userId);

        assertEquals(testItemRequestDto1, result);
    }

    @Test
    void createRequestTest_ifNoSuchUser_NotFoundException() {
        long userId = 0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.createRequest(testItemRequestDto1, userId));
    }

    @Test
    void getRequestTest_ifOk_returnItemRequestResponseDto() {
        long userId = 1;
        long requestId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(testUser1));
        when(requestRepository.findById(requestId)).thenReturn(Optional.ofNullable(testItemRequest1));
        when(itemRepository.findAllByRequestIdEquals(requestId)).thenReturn(List.of(itemForResponseDto));

        ItemRequestResponseDto result = requestService.getRequest(userId, requestId);

        assertEquals(testItemRequestResponseDto, result);
    }

    @Test
    void getRequestTest_ifNoSuchUser_NotFoundException() {
        long userId = 0;
        long requestId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequest(userId, requestId));
    }

    @Test
    void getRequestTest_ifNoSuchRequest_NotFoundException() {
        long userId = 1;
        long requestId = 0;
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(testUser1));
        when(requestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequest(userId, requestId));
    }

    @Test
    void getRequestsTest_ifOk_returnItemRequestResponseDto() {
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(testUser1));
        when(requestRepository.searchByRequestor(userId)).thenReturn(List.of(testItemRequest1));
        when(itemRepository.findAllByRequestIdEquals(testItemRequest1.getId())).thenReturn(List.of(itemForResponseDto));

        List<ItemRequestResponseDto> result = requestService.getRequests(userId);

        assertEquals(List.of(testItemRequestResponseDto), result);
    }

    @Test
    void getRequestsTest_ifNoSuchUser_NotFoundException() {
        long userId = 0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.getRequests(userId));
    }

    @Test
    void searchRequestsTest_ifOk_returnItemRequestResponseDtoList() {
        long userId = 1;
        Pageable pageParams = PageRequest.of(
                fromToPage(0, 10), 10, Sort.by(Sort.Direction.DESC, "created"));
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(testUser1));
        when(requestRepository.searchAllPageable(userId, pageParams))
                .thenReturn(new PageImpl<>(List.of(testItemRequest1)));
        when(itemRepository.findAllByRequestIdEquals(testItemRequest1.getId()))
                .thenReturn(List.of(itemForResponseDto));

        List<ItemRequestResponseDto> result = requestService.searchRequests(userId, 0, 10);

        assertEquals(List.of(testItemRequestResponseDto), result);
    }

    @Test
    void searchRequestsTest_ifNoSuchUser_NotFoundException() {
        long userId = 0;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.searchRequests(userId, 0, 10));
    }

    private int fromToPage(int from, int size) {
        float result = (float) from / size;
        return (int) Math.ceil(result);
    }
}
