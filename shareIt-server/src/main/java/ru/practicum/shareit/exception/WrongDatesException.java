package ru.practicum.shareit.exception;

public class WrongDatesException extends RuntimeException {
    public WrongDatesException(final String message) {
        super(message);
    }
}
