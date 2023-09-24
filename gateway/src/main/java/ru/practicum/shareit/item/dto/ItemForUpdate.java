package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemForUpdate {
    @Size(min = 1, max = 255, message = "Допустимый размер имени - от 1 до 255 знаков.")
    private String name;
    @Size(min = 1, max = 512, message = "Допустимый размер описания - от 1 до 512 знаков.")
    private String description;
    private Boolean available;
    private Long requestId;
    private long ownerId;
}
