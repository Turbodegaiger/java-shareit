package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForUpdateDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    @Autowired
    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto user) {
        log.info("Принят запрос на создание пользователя с параметрами: {}", user);
        return userService.createUser(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Принят запрос на удаление пользователя с id = {}", userId);
        userService.removeUser(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody @Valid UserForUpdateDto user, @PathVariable long userId) {
        log.info("Принят запрос на обновление пользователя с id = {}", userId);
        return userService.updateUser(user, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable long userId) {
        log.info("Принят запрос на получение пользователя с id = {}", userId);
        return userService.getUser(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Принят запрос на получение списка пользователей.");
        return userService.getUsers();
    }
}
