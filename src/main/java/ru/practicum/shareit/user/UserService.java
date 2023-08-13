package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto createUser(User user);

    void removeUser(long id);

    UserDto updateUser(User user, long userId);

    UserDto getUser(long id);

    List<UserDto> getUsers();
}
