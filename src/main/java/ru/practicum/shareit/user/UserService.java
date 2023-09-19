package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForUpdateDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto user);

    void removeUser(long id);

    UserDto updateUser(UserForUpdateDto user, long userId);

    UserDto getUser(long id);

    List<UserDto> getUsers();
}
