package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemForResponseDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemRequestResponseDto {
    private long id;
    @NotEmpty(message = "Описание не может быть null")
    @Size(min = 1, max = 512, message = "Допустимый размер имени - от 1 до 255 знаков.")
    private String description;
    private long requesterId;
    @NotNull
    private String created;
    private List<ItemForResponseDto> items;
}
