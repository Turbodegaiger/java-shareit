package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemForResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestServiceImpl requestService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    String userIdHeader = "X-Sharer-User-Id";
    ItemForResponseDto itemForResponseDto = new ItemForResponseDto(1, "predmet", "prosto predmet", true, null);
    User user1 = new User(1L, "user1", "user1@ya.ru");
    ItemRequest testItemRequest1 = new ItemRequest(1, "nuzhen predmet", user1, LocalDateTime.now());
    ItemRequestDto testItemRequestDto1 = ItemRequestMapper.toItemRequestDto(testItemRequest1);
    ItemRequestResponseDto testItemRequestResponseDto = ItemRequestMapper.toItemRequestResponseDto(testItemRequest1, List.of(itemForResponseDto));
    UserDto testUserDto1 = new UserDto(1, "user1", "user1@ya.ru");

    @SneakyThrows
    @Test
    void createRequestTest_ifValid_returnItemRequestDto() {
        when(requestService.createRequest(testItemRequestDto1, testUserDto1.getId()))
                .thenReturn(testItemRequestDto1);

        String result = mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(testItemRequestDto1))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(testItemRequestDto1), result);
    }

    @SneakyThrows
    @Test
    void getRequestsTest_ifOk_returnItemRequestResponseDtoList() {
        when(requestService.getRequests(1))
                .thenReturn(List.of(testItemRequestResponseDto));

        String result = mvc.perform(get("/requests")
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(testItemRequestResponseDto)), result);
    }

    @SneakyThrows
    @Test
    void getRequestsTest_ifNoSuchUser_NotFoundException() {
        doThrow(NotFoundException.class).when(requestService).getRequest(0, 1);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header(userIdHeader, 0)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getRequestTest_ifOk_returnItemRequestResponseDtoList() {
        when(requestService.getRequest(1, 1))
                .thenReturn(testItemRequestResponseDto);

        String result = mvc.perform(get("/requests/{requestId}", 1)
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(testItemRequestResponseDto), result);
    }

    @SneakyThrows
    @Test
    void searchRequestTest_ifOk_returnRequestResponseDtoList() {
        when(requestService.searchRequests(1, 0, 10))
                .thenReturn(List.of(testItemRequestResponseDto));

        String result = mvc.perform(get("/requests/all")
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(testItemRequestResponseDto)), result);
    }

    @SneakyThrows
    @Test
    void searchRequestTest_ifNoSuchUser_NotFoundException() {
        doThrow(NotFoundException.class).when(requestService).searchRequests(0, 1, 1);

        mvc.perform(patch("/items/all")
                        .contentType("application/json")
                        .header(userIdHeader, 0)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
