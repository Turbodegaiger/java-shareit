package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class Item {
    private long id;
    @NotEmpty(message = "Имя не может быть null")
    @Size(min = 1, max = 50)
    private String name;
    @NotEmpty(message = "Описание не может быть null")
    @Size(min = 1, max = 200)
    private String description;
    @NotNull(message = "Поле available не может быть null")
    private Boolean available;
    @NotNull(message = "Владелец не может быть null")
    private Long owner;

    public Item(long id, String name, String description, Boolean available, Long owner) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        //this.request = request;
    }
}

