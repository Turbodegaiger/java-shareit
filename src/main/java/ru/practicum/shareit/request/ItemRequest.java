package ru.practicum.shareit.request;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    long id;
    String description;
    long requestor;
    LocalDateTime created;

    public ItemRequest(long id, String description, long requestor, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.requestor = requestor;
        this.created = created;
    }
}
