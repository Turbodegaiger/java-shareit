package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@Slf4j
public class UserDao {
    private final HashMap<Long, User> users = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();
    private static Long currentId = 0L;

    public User createUser(User user) {
        if (emailUniqSet.contains(user.getEmail())) {
            log.info("Невозможно создать пользователя {}, email {} уже существует.", user.getName(), user.getEmail());
            throw new AlreadyExistsException(
                    String.format("Невозможно создать пользователя %s, email %s уже существует.", user.getName(), user.getEmail()));
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        emailUniqSet.add(user.getEmail());
        User returnValue = users.get(user.getId());
        log.info("Создан пользователь id = {} с email {}.", user.getId(), user.getEmail());
        return returnValue;
    }

    public User removeUser(Long id) {
        User removedUser = users.remove(id);
        if (removedUser == null) {
            log.info("Невозможно удалить. Пользователь с id = {} не найден.", id);
            throw new NotFoundException("Невозможно удалить. Пользователь с id = " + id + " не найден.");
        }
        emailUniqSet.remove(removedUser.getEmail());
        log.info("Пользователь с id = {} удалён.", id);
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
        if (emailUniqSet.contains(newEmail) && !newEmail.equals(updatedUser.getEmail())) {
            log.info("Обновление невозможно: пользователь с email {} уже существует.", newEmail);
            throw new AlreadyExistsException("Обновление невозможно: пользователь с email " + newEmail + " уже существует.");
        }
        if (newName != null && !newName.equals("")) {
            updatedUser.setName(newName);
        }
        if (newEmail != null && newEmail.contains("@")) {
            emailUniqSet.remove(updatedUser.getEmail());
            emailUniqSet.add(newEmail);
            updatedUser.setEmail(newEmail);
        }
        users.replace(userId, updatedUser);
        User returnValue = users.get(userId);
        log.info("Пользователь с id = {} обновлён.", userId);
        return returnValue;
    }

    public User getUser(Long id) {
        User user = users.get(id);
        if (user == null) {
            log.info("Невозможно выгрузить. Пользователь с id = {} не найден.", id);
            throw new NotFoundException("Невозможно выгрузить. Пользователь с id = " + id + " не найден.");
        }
        log.info("Пользователь с id = {} выгружен.", user.getId());
        return user;
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
