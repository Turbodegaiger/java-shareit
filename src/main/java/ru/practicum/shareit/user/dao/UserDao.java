package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class UserDao {
    private final HashMap<Long, User> users = new HashMap<>();
    private static Long currentId = 0L;

    public User createUser(User user) {
        List<User> userList = users.values().stream()
                        .filter(user1 -> user1.getEmail().equals(user.getEmail()))
                        .collect(Collectors.toList());
        if (userList.isEmpty()) {
            user.setId(generateId());
            users.put(user.getId(), user);
            User returnValue = users.get(user.getId());
            log.info("Создан пользователь id = {} с email {}.", user.getId(), user.getEmail());
            return returnValue;
        } else {
            log.info("Невозможно создать пользователя {}, email {} уже существует.", user.getName(), user.getEmail());
            throw new AlreadyExistsException(
                    String.format("Невозможно создать пользователя %s, email %s уже существует.", user.getName(), user.getEmail()));
        }
    }

    public User removeUser(Long id) {
        User removedUser = users.remove(id);
        if (removedUser != null) {
            log.info("Пользователь с id = {} удалён.", id);
        } else {
            log.info("Невозможно удалить. Пользователь с id = {} не найден.", id);
            throw new NotFoundException("Невозможно удалить. Пользователь с id = " + id + " не найден.");
        }
        return removedUser;
    }

    public User updateUser(User user, long userId) {
        User updatedUser = users.get(userId);
        if (updatedUser == null) {
            log.info("Невозможно обновить. Пользователь с id = {} не найден.", userId);
            throw new NotFoundException("Невозможно обновить. Пользователь с id = " + userId + " не найден.");
        }
        String newName = user.getName();
        String newEmail = user.getEmail();
        List<User> sameEmail = users.values().stream()
                .filter(user1 -> user1.getEmail().equals(newEmail))
                .filter(user1 -> !user1.getEmail().equals(updatedUser.getEmail()))
                .collect(Collectors.toList());
        if (sameEmail.isEmpty()) {
            if (newName != null && !newName.equals("")) {
                updatedUser.setName(user.getName());
            }
            if (newEmail != null && newEmail.contains("@")) {
                updatedUser.setEmail(user.getEmail());
            }
            users.replace(userId, updatedUser);
            User returnValue = users.get(userId);
            log.info("Пользователь с id = {} обновлён.", userId);
            return returnValue;
        } else {
            log.info("Обновление невозможно: пользователь с email {} уже существует.", newEmail);
            throw new AlreadyExistsException("Обновление невозможно: пользователь с email " + newEmail + " уже существует.");
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

    public List<User> getUsers() {
        List<User> userList = new ArrayList<>(users.values());
        log.info("Выгружен список пользователей размером {} записей.", userList.size());
        return userList;
    }

    private Long generateId() {
        return ++currentId;
    }
}
