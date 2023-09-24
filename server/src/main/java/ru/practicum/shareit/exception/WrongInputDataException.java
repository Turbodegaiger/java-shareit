package ru.practicum.shareit.exception;

public class WrongInputDataException extends RuntimeException {
    public WrongInputDataException(String message) {
        super(message);
    }
}