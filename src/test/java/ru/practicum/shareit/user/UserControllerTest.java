package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForUpdateDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    UserDto userDto1 = new UserDto(1, "user1", "user1@ya.ru");

    @SneakyThrows
    @Test
    void createUserTest_ifValid_returnUserDto() {
        when(userService.createUser(userDto1))
                .thenReturn(userDto1);

        String result = mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(userDto1), result);
    }

    @SneakyThrows
    @Test
    void createUserTest_ifInvalidEmail_responseBadRequest() {
        UserDto invalidUserDto = userDto1;
        invalidUserDto.setEmail("aiaia");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        invalidUserDto.setEmail("");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        invalidUserDto.setEmail("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@ya.ru");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @SneakyThrows
    @Test
    void createUserTest_ifInvalidName_responseBadRequest() {
        UserDto invalidUserDto = userDto1;
        invalidUserDto.setName("");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        invalidUserDto.setName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(invalidUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any());
    }

    @SneakyThrows
    @Test
    void deleteUserTest_ifUserExists_responseIsOk() {
        doNothing().when(userService).removeUser(1L);
        mvc.perform(delete("/users/{userId}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void deleteUserTest_ifUserNotExists_responseIsNotFound() {
        doThrow(NotFoundException.class).when(userService).removeUser(0L);
        mvc.perform(delete("/users/{userId}", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateUserTest_ifUserExists_returnUpdatedUser() {
        UserDto update = new UserDto(1, "user10", "user1@ya.ru");
        UserForUpdateDto updateDto = new UserForUpdateDto("user10", "user1@ya.ru");
        when(userService.updateUser(updateDto, 1))
                .thenReturn(update);

        String result = mvc.perform(patch("/users/{userId}", "1")
                        .content(mapper.writeValueAsString(update))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(update), result);
    }

    @SneakyThrows
    @Test
    void updateUserTest_ifUserNotExists_NotFoundException() {
        UserForUpdateDto updateDto = new UserForUpdateDto("user30", "user3@ya.ru");
        when(userService.updateUser(updateDto, 3))
                .thenThrow(NotFoundException.class);

        mvc.perform(patch("/users/{userId}", "3")
                        .content(mapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateUserTest_ifEmailConflict_AlreadyExistsException() {
        UserForUpdateDto updateDto = new UserForUpdateDto("user2", "user2@ya.ru");
        when(userService.updateUser(updateDto, 1))
                .thenThrow(AlreadyExistsException.class);

        mvc.perform(patch("/users/{userId}", "1")
                        .content(mapper.writeValueAsString(updateDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @SneakyThrows
    @Test
    void getUserTest_ifFound_returnUserDto() {
        when(userService.getUser(userDto1.getId())).thenReturn(userDto1);

        String result = mvc.perform(get("/users/{userId}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(userDto1), result);
    }

    @SneakyThrows
    @Test
    void getUserTest_ifNotFound_NotFoundException() {
        when(userService.getUser(3)).thenThrow(NotFoundException.class);

        mvc.perform(get("/users/{userId}", "3")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getUsersTest_ifInvoked_returnUserDtoList() {
        when(userService.getUsers()).thenReturn(List.of(userDto1));

        String result = mvc.perform(get("/users/")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(mapper.writeValueAsString(List.of(userDto1)), result);
    }
}
