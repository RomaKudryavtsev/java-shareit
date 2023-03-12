package ru.practicum.shareit.exception;

public class NonOwnerUpdatingException extends RuntimeException {
    public NonOwnerUpdatingException() {
    }

    public NonOwnerUpdatingException(final String message) {
        super(message);
    }
}
