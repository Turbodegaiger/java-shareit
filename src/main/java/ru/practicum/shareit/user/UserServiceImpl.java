package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final HashMap <Long, User> users = new HashMap<>();
    private static Long currentId = 0L;

    public User createUser(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Создан пользователь id = {}", user.getId());
        return user;
    }

    public void removeUser(Long id) {
        User removedUser = users.remove(id);
        if (removedUser != null) {
            log.info("Пользователь с id = {} удалён.", id);
        } else {
            log.info("Невозможно удалить. Пользователь с id = {} не найден.", id);
            throw new NotFoundException("Невозможно удалить. Пользователь с id = " + id + " не найден.");
        }
    }

    public User updateUser(User user) {
        User updatedUser = users.replace(user.getId(), user);
        if (updatedUser != null) {
            log.info("Пользователь с id = {} обновлён.", user.getId());
            return updatedUser;
        } else {
            log.info("Невозможно обновить. Пользователь с id = {} не найден.", user.getId());
            throw new NotFoundException("Невозможно обновить. Пользователь с id = " + user.getId() + " не найден.");
        }
    }

    public User getUser(Long id) {
        User user = users.get(id);
        if (user != null) {
            log.info("Пользователь с id = {} выгружен.", user.getId());
            return user;
        } else {
            log.info("Невозможно выгрузить. Пользователь с id = {} не найден.", id);
            throw new NotFoundException("Невозможно выгрузить. Пользователь с id = " + id + " не найден.");
        }
    }

    private Long generateId() {
        return ++currentId;
    }
}
