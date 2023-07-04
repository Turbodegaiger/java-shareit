package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    long id;
    @Size(min = 1, max = 50)
    String name;
    @Size(max = 200)
    String description;
    boolean available;
    long request;

    public ItemDto(long id, String name, String description, boolean available, long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }
}