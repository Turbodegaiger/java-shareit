package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemForUpdate {
    @Size(min = 1, max = 255)
    private String name;
    @Size(min = 1, max = 512)
    private String description;
    private Boolean available;
    private long requestId;
    private long ownerId;
}
