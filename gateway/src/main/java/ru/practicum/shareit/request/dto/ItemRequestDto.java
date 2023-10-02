package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private long id;
    @NotEmpty(message = "Описание не может быть null")
    @Size(min = 1, max = 512, message = "Допустимый размер описания - от 1 до 512 знаков.")
    private String description;
    private long requesterId;
    private String created;
}
