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
    private long id;
    @NotEmpty(message = "Имя пользователя не может быть null")
    @Size(min = 1, max = 50, message = "Допустимый размер имени - от 1 до 50 знаков.")
    private String name;
    @NotEmpty(message = "Email не может быть null")
    @Email(message = "Некорректный email")
    @Size(min = 1, max = 100, message = "Допустимый размер email - от 1 до 100 знаков.")
    private String email;
}
