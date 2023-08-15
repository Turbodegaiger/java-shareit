package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @NotEmpty(message = "Имя пользователя не может быть null")
    @Size(min = 1, max = 50)
    private String name;
    @NotEmpty(message = "email не может быть null")
    @Email
    @Size(min = 1, max = 100)
    private String email;
}
