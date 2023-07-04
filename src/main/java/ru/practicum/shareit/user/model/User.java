package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    long id;
    @Size(min = 1, max = 30)
    String name;
    @Email
    String email;

    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
