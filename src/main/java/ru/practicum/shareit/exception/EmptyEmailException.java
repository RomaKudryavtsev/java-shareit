package ru.practicum.shareit.exception;

public class EmptyEmailException extends RuntimeException {
    public EmptyEmailException() {
    }

    public EmptyEmailException(final String message) {
        super(message);
    }

}
