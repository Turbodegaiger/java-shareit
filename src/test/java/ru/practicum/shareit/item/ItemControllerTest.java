package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUpdate;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    String userIdHeader = "X-Sharer-User-Id";
    ItemDto testItemDto1 = new ItemDto(1, "predmet", "prosto predmet", true, 0, 1);
    ItemForUpdate testItemUpdateDto1 = new ItemForUpdate("NEWpredmet", "prosto predmet", true, 0, 1);
    ItemCommentDto testItemCommentDto1 = new ItemCommentDto(
            1,
            "predmet",
            "prosto predmet",
            true, 0,
            new BookingForItemDto(),
            new BookingForItemDto(),
            List.of(new CommentDto()));
    ItemDto updateTest = new ItemDto(1, "NEWpredmet", "prosto predmet", true, 0, 1);
    UserDto testUserDto1 = new UserDto(1, "user1", "user1@ya.ru");
    CommentDto testCommentDto = new CommentDto(
            1,
            "predmet prosto vayyyy",
            testItemDto1.getId(),
            testUserDto1.getName(),
            LocalDateTime.now().toString());


    @SneakyThrows
    @Test
    void createItemTest_ifValid_returnItemDto() {
        when(itemService.createItem(testItemDto1, testUserDto1.getId()))
                .thenReturn(testItemDto1);

        String result = mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(testItemDto1))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(testItemDto1), result);
    }

    @SneakyThrows
    @Test
    void createItemTest_ifInvalidDto_responseBadRequest() {
        StringBuilder sb = new StringBuilder();
        ItemDto invalidItemDto = testItemDto1;

        invalidItemDto.setName("");
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(invalidItemDto))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        sb.append("a".repeat(256));
        invalidItemDto.setName(sb.toString());
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(invalidItemDto))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        sb.append("a".repeat(256));
        invalidItemDto.setDescription(sb.toString());
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(invalidItemDto))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        invalidItemDto.setDescription("");
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(invalidItemDto))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        invalidItemDto.setAvailable(null);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(invalidItemDto))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).createItem(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void createItemTest_ifNoSuchUser_responseNotFound() {
        doThrow(NotFoundException.class).when(itemService).createItem(testItemDto1, 2);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(testItemDto1))
                        .contentType("application/json")
                        .header(userIdHeader, 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void createItemTest_ifIncorrectRequest_responseBadRequest() {
        doThrow(ValidationException.class).when(itemService).createItem(testItemDto1, 1);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(testItemDto1))
                        .contentType("application/json")
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateItemTest_ifValid_returnUpdatedItemDto() {
        ItemForUpdate update = testItemUpdateDto1;
        when(itemService.updateItem(update, 1, 1))
                .thenReturn(updateTest);

        String result = mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(update))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(updateTest), result);
    }

    @SneakyThrows
    @Test
    void updateItemTest_ifInvalidDto_responseBadRequest() {
        ItemDto update = updateTest;
        StringBuilder sb = new StringBuilder();

        update.setName("");
        mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(update))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        sb.append("a".repeat(256));
        update.setName(sb.toString());
        mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(update))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        sb.append("a".repeat(256));
        update.setDescription(sb.toString());
        mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(update))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        update.setDescription("");
        mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(update))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        update.setAvailable(null);
        mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(update))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).updateItem(any(), anyLong(), anyLong());
    }

    @SneakyThrows
    @Test
    void updateItemTest_ifNoSuchItem_responseNotFound() {
        doThrow(NotFoundException.class).when(itemService).updateItem(testItemUpdateDto1, 1, 1);

        mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(testItemUpdateDto1))
                        .contentType("application/json")
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateItemTest_ifNoSuchUser_responseNotFound() {
        doThrow(NotFoundException.class).when(itemService).updateItem(testItemUpdateDto1, 1, 2);

        mvc.perform(patch("/items/{itemId}", "1")
                        .content(mapper.writeValueAsString(testItemUpdateDto1))
                        .contentType("application/json")
                        .header(userIdHeader, 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getItemTest_ifInvoked_responseIsOk() {
        when(itemService.getItem(testItemCommentDto1.getId(), testUserDto1.getId()))
                .thenReturn(testItemCommentDto1);

        String result = mvc.perform(get("/items/{itemId}", 1)
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(testItemCommentDto1), result);
    }

    @SneakyThrows
    @Test
    void getItemTest_ifNoSuchUser_responseNotFound() {
        doThrow(NotFoundException.class).when(itemService).getItem(1, 2);

        mvc.perform(get("/items/{itemId}", "1")
                        .header(userIdHeader, 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getItemTest_ifNoSuchItem_responseNotFound() {
        doThrow(NotFoundException.class).when(itemService).getItem(2, 1);

        mvc.perform(get("/items/{itemId}", "2")
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getItemsTest_ifInvoked_responseIsOk() {
        when(itemService.getItems(testItemCommentDto1.getId(), 0, 10))
                .thenReturn(List.of(testItemCommentDto1));

        String result = mvc.perform(get("/items")
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(testItemCommentDto1)), result);
    }

    @SneakyThrows
    @Test
    void getItemsTest_ifNoSuchUser_responseNotFound() {
        doThrow(NotFoundException.class).when(itemService).getItems(2, 0, 10);

        mvc.perform(get("/items")
                        .content(mapper.writeValueAsString(updateTest))
                        .header(userIdHeader, 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void searchItemsTest_ifOk_returnItemDtoList() {
        when(itemService.searchItems("it", 0, 10))
                .thenReturn(List.of(testItemDto1));

        String result = mvc.perform(get("/items/search?text=it")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(testItemDto1)), result);
    }

    @SneakyThrows
    @Test
    void searchItemsTest_ifNoText_returnEmptyList() {
        when(itemService.searchItems("", 0, 10))
                .thenReturn(Collections.emptyList());

        String result = mvc.perform(get("/items/search?text=")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(Collections.emptyList()), result);
    }

    @SneakyThrows
    @Test
    void createCommentTest_ifOk_returnCommentDto() {
        when(itemService.createComment(testCommentDto, testItemDto1.getId(), testUserDto1.getId()))
                .thenReturn(testCommentDto);

        String result = mvc.perform(post("/items/{itemId}/comment", "1")
                        .content(mapper.writeValueAsString(testCommentDto))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(testCommentDto), result);
    }

    @SneakyThrows
    @Test
    void createCommentTest_ifInvalidDto_returnBadRequest() {
        StringBuilder sb = new StringBuilder();
        CommentDto invalidTestCommentDto = testCommentDto;

        invalidTestCommentDto.setText("");
        mvc.perform(post("/items/{itemId}/comment", "1")
                        .content(mapper.writeValueAsString(invalidTestCommentDto))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        sb.append("a".repeat(513));
        invalidTestCommentDto.setText(sb.toString());
        mvc.perform(post("/items/{itemId}/comment", "1")
                        .content(mapper.writeValueAsString(invalidTestCommentDto))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createCommentTest_ifNoSuchUser_returnNotFound() {
        doThrow(NotFoundException.class).when(itemService).createComment(testCommentDto, testItemDto1.getId(), 2);
        mvc.perform(post("/items/{itemId}/comment", "1")
                        .content(mapper.writeValueAsString(testCommentDto))
                        .header(userIdHeader, 2)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void createCommentTest_ifNoSuchItem_returnNotFound() {
        doThrow(NotFoundException.class).when(itemService).createComment(testCommentDto, 2, testUserDto1.getId());
        mvc.perform(post("/items/{itemId}/comment", "2")
                        .content(mapper.writeValueAsString(testCommentDto))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void createCommentTest_ifUserNotABooker_returnBadRequest() {
        doThrow(NoAccessException.class).when(itemService).createComment(testCommentDto, testItemDto1.getId(), testUserDto1.getId());
        mvc.perform(post("/items/{itemId}/comment", "1")
                        .content(mapper.writeValueAsString(testCommentDto))
                        .header(userIdHeader, 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
