package ru.practicum.shareit.exception;

public class EmptyItemNameException extends RuntimeException {
    public EmptyItemNameException() {
    }

    public EmptyItemNameException(final String message) {
        super(message);
    }
}
