package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
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
    public User updateUser(@RequestBody User user, @PathVariable long userId) {
        log.info("Принят запрос на обновление пользователя с id = {}", userId);
        return userService.updateUser(user, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{userId}")
    public User getUser(@PathVariable long userId) {
        log.info("Принят запрос на получение пользователя с id = {}", userId);
        return userService.getUser(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<User> getUsers() {
        log.info("Принят запрос на получение списка пользователей.");
        return userService.getUsers();
    }
}
