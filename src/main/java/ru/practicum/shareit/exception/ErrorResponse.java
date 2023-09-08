package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }
}
