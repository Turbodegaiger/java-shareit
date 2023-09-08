package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserForUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    UserDto userDto1 = new UserDto(1, "user1", "user1@ya.ru");
    User user1 = new User(1L, "user1", "user1@ya.ru");

    @Test
    void createUserTest_ifValid_returnUser() {
        when(userRepository.save(UserMapper.mapToNewUser(userDto1))).thenReturn(UserMapper.mapToNewUser(userDto1));

        UserDto result = userService.createUser(userDto1);

        assertEquals(userDto1, result);
    }

    @Test
    void createUserTest_ifInvalidEmail_ValidationException() {
        UserDto userInvalidDto1 = new UserDto(1, "user1", "usu");

        assertThrows(ValidationException.class, () -> userService.createUser(userInvalidDto1));
    }

    @Test
    void removeUserTest_ifNoSuchUser_NotFoundException() {
        doThrow(IllegalArgumentException.class).when(userRepository).deleteById(0L);

        assertThrows(NotFoundException.class, () -> userService.removeUser(0L));
    }

    @Test
    void updateUserTest_ifValid_returnUserDto() {
        UserDto update = new UserDto(1, "user10", "user1@ya.ru");
        UserForUpdateDto updateDto = new UserForUpdateDto("user10", "user1@ya.ru");
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(UserMapper.mapToNewUser(userDto1)));
        when(userRepository.save(UserMapper.mapToNewUser(update)))
                .thenReturn(UserMapper.mapToNewUser(update));

        UserDto updatedUser = userService.updateUser(updateDto, update.getId());

        assertEquals(updatedUser, update);
    }

    @Test
    void updateUserTest_ifEmailConflict_AlreadyExistsException() {
        UserDto update = new UserDto(1L, "user1", "user2@ya.ru");
        UserForUpdateDto updateDto = new UserForUpdateDto("user1", "user2@ya.ru");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(UserMapper.mapToNewUser(userDto1)));
        when(userRepository.findByEmailEquals(update.getEmail()))
                .thenReturn(new User(2L, "user2", "user2@ya.ru"));

        assertThrows(AlreadyExistsException.class, () -> userService.updateUser(updateDto, update.getId()));
    }

    @Test
    void updateUserTest_ifNoSuchUser_NotFoundException() {
        UserDto update = new UserDto(3L, "user1", "user2@ya.ru");
        UserForUpdateDto updateDto = new UserForUpdateDto("user1", "user2@ya.ru");
        when(userRepository.findById(3L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(updateDto, update.getId()));
    }

    @Test
    void getUserTest_ifExists_returnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        UserDto user = userService.getUser(1L);

        assertEquals(user, userDto1);
    }

    @Test
    void getUserTest_ifNotExists_NotFoundException() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(3L));
    }

    @Test
    void getUsersTest_ifInvocated_returnsUsersList() {
        when(userRepository.findAll()).thenReturn(List.of(user1));

        List<UserDto> users = userService.getUsers();

        assertEquals(List.of(userDto1), users);
    }
}
