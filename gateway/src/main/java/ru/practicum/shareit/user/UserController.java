package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForUpdateDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;
    private final String userIdHeader = "X-Sharer-User-Id";
    
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto user) {
        log.info("Принят запрос на создание пользователя с параметрами: {}", user);
        return userClient.createUser(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Принят запрос на удаление пользователя с id = {}", userId);
        userClient.removeUser(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody @Valid UserForUpdateDto user, @PathVariable long userId) {
        log.info("Принят запрос на обновление пользователя с id = {}", userId);
        return userClient.updateUser(user, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("Принят запрос на получение пользователя с id = {}", userId);
        return userClient.getUser(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Принят запрос на получение списка пользователей.");
        return userClient.getUsers();
    }
}
