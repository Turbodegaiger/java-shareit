package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private long id;
    @NotEmpty(message = "Имя пользователя не может быть null")
    @Size(min = 1, max = 30)
    private String name;
    @NotEmpty(message = "email не может быть null")
    @Email
    private String email;

    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
