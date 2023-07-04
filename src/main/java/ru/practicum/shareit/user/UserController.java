package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Принят запрос на создание пользователя с параметрами: {}", user);
        return userService.createUser(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping
    public void deleteUser(@RequestParam Long id) {
        log.info("Принят запрос на удаление пользователя с id = {}", id);
        userService.removeUser(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Принят запрос на обновление пользователя с id = {}", user.getId());
        return userService.updateUser(user);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public User getUser(@RequestParam Long id) {
        log.info("Принят запрос на получение пользователя с id = {}", id);
        return userService.getUser(id);
    }
}
