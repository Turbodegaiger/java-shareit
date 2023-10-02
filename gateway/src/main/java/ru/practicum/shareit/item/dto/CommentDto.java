package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private long id;
    @NotNull(message = "Комментарий должен содержать от 1 до 512 символов.")
    @Size(min = 1, max = 512, message = "Допустимый размер комментария - от 1 до 512 знаков.")
    private String text;
    private long itemId;
    private String authorName;
    private String created;
}
