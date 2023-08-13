package ru.practicum.shareit.user.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class UserMapper {

    public static UserDto mapToUserDto(User user) {
        return new UserDto(user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static List<UserDto> mapToUserDto(Iterable<User> users) {
        List<UserDto> result = new ArrayList<>();
        for (User user : users) {
            result.add(mapToUserDto(user));
        }
        return result;
    }

    public static User mapToNewUser(UserDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        return user;
    }
}