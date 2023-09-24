package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class ItemForResponseDto {
    private long id;
    @NotEmpty(message = "Имя не может быть null")
    @Size(min = 1, max = 255, message = "Допустимый размер имени - от 1 до 255 знаков.")
    private String name;
    @NotEmpty(message = "Описание не может быть null")
    @Size(min = 1, max = 512, message = "Допустимый размер описания - от 1 до 512 знаков.")
    private String description;
    @NotNull(message = "Поле available не может быть null")
    private Boolean available;
    private Long requestId;
}