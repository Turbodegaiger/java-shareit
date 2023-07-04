package ru.practicum.shareit.item.model;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    long id;
    @Size(min = 1, max = 50)
    String name;
    @Size(max = 200)
    String description;
    boolean isAvailable;
    long owner;
    long request;

    public Item(long id, String name, String description, boolean isAvailable, long owner, long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAvailable = isAvailable;
        this.owner = owner;
        this.request = request;
    }
}

