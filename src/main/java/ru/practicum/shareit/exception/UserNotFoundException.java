package ru.practicum.shareit.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {}

    public UserNotFoundException(final String message) {
        super(message);
    }
}
