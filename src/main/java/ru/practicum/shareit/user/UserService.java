package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User createUser(User user);

    void removeUser(Long id);

    User updateUser(User user, long userId);

    User getUser(Long id);

    List<User> getUsers();
}
