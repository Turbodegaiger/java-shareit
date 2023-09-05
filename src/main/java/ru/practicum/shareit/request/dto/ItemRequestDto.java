package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-itemId-requests.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    long id;
    @NotEmpty(message = "Описание не может быть null")
    @Size(min = 1, max = 512)
    String description;
    long requestorId;
    String created;
}
