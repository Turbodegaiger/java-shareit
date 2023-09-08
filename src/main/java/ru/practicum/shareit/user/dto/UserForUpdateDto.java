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
    @Size(min = 1, max = 50)
    private String name;
    @Email
    @Size(min = 1, max = 100)
    private String email;
}