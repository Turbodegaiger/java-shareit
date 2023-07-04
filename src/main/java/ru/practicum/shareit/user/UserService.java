package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

public interface UserService {
    User createUser(User user);
    void removeUser(Long id);
    User updateUser(User user);
    User getUser(Long id);
}
