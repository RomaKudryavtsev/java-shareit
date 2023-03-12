package ru.practicum.shareit.exception;

public class EmailAlreadyInUseException extends RuntimeException {
    public EmailAlreadyInUseException() {}

    public EmailAlreadyInUseException(final String message) {
        super(message);
    }
}
