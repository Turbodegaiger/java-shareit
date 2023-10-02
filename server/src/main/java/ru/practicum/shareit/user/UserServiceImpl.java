package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private final UserRepository repository;

    public UserDto createUser(UserDto user) {
        User newUser = repository.save(UserMapper.mapToNewUser(user));
        log.info("Создан пользователь {}.", newUser);
        return UserMapper.mapToUserDto(newUser);
    }

    public void removeUser(long userId) {
        try {
            repository.deleteById(userId);
        } catch (IllegalArgumentException exception) {
            throw new NotFoundException("Невозможно обновить. Пользователь с id = " + userId + " не найден.");
        }
        log.info("Пользователь с id = {} удалён.", userId);
    }

    public UserDto updateUser(UserForUpdateDto userUpdate, long userId) {
        User oldUser = repository.findById(userId).orElseThrow(
                () -> new NotFoundException("Невозможно обновить. Пользователь с id = " + userId + " не найден."));
        UserDto user = UserMapper.mapToUser(userUpdate, userId);
        if (user.getEmail() != null && !user.getEmail().equals(oldUser.getEmail())) {
            User sameEmailUser = repository.findByEmailEquals(user.getEmail());
            if (sameEmailUser != null) {
                log.info("Невозможно создать пользователя {}, email {} занят.", user.getName(), user.getEmail());
                throw new AlreadyExistsException(
                        String.format("Невозможно создать пользователя %s, email %s занят.", user.getName(), user.getEmail()));
            }
        }
        user.setId(oldUser.getId());
        if (user.getName() == null) {
            user.setName(oldUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(oldUser.getEmail());
        }
        User updatedUser = repository.save(UserMapper.mapToNewUser(user));
        log.info("Пользователь обновлён {}.", updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public UserDto getUser(long id) {
        User user = repository.findById(id).orElseThrow(
                () -> new NotFoundException("Невозможно выгрузить. Пользователь с id = " + id + " не найден."));
        log.info("Пользователь с id = {} выгружен.", user.getId());
        return UserMapper.mapToUserDto(user);
    }

    public List<UserDto> getUsers() {
        List<User> users = repository.findAll();
        log.info("Выгружен список пользователей размером {} записей.", users.size());
        return UserMapper.mapToUserDto(users);
    }
}
