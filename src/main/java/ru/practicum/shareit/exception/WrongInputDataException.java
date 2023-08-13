package ru.practicum.shareit.exception;

public class WrongInputDataException extends RuntimeException {
    public WrongInputDataException() {
    }

    public WrongInputDataException(String message) {
        super(message);
    }

    public WrongInputDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongInputDataException(Throwable cause) {
        super(cause);
    }
}