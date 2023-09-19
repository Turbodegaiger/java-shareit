package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class ItemForResponseDto {
    private long id;
    @NotEmpty(message = "Имя не может быть null")
    @Size(min = 1, max = 255)
    private String name;
    @NotEmpty(message = "Описание не может быть null")
    @Size(min = 1, max = 512)
    private String description;
    @NotNull(message = "Поле available не может быть null")
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Long requestId;
}