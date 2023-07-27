package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("Невозможно создать пользователя {}, некорректный email.", user.getName());
            throw new ValidationException(
                    String.format("Невозможно создать пользователя %s, некорректный email.", user.getName()));
        }
        return userDao.createUser(user);
    }

    public void removeUser(Long id) {
        userDao.removeUser(id);
    }

    public User updateUser(User user, long userId) {
        return userDao.updateUser(user, userId);
    }

    public User getUser(Long id) {
        return userDao.getUser(id);
    }

    public List<User> getUsers() {
        return userDao.getUsers();
    }
}
