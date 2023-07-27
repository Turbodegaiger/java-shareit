package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ItemDto {
    private long id;
    @NotEmpty(message = "Имя не может быть null")
    @Size(min = 1, max = 50)
    private String name;
    @NotEmpty(message = "Описание не может быть null")
    @Size(min = 1, max = 200)
    private String description;
    @NotNull(message = "Поле available не может быть null")
    private Boolean available;

    public ItemDto(long id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }
}