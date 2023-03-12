package ru.practicum.shareit.exception;

public class EmptyItemDescriptionException extends RuntimeException {
    public EmptyItemDescriptionException() {}

    public EmptyItemDescriptionException(final String message) {
        super(message);
    }
}
