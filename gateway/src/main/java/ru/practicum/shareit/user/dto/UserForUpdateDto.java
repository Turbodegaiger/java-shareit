package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserForUpdateDto {
    @Size(min = 1, max = 50, message = "Допустимый размер имени - от 1 до 50 знаков.")
    private String name;
    @Email(message = "Некорректный email")
    @Size(min = 1, max = 100, message = "Допустимый размер email - от 1 до 100 знаков.")
    private String email;
}