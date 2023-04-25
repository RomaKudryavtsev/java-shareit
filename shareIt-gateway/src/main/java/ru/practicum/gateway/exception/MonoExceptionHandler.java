package ru.practicum.gateway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MonoExceptionHandler {

    @ExceptionHandler(MonoException.class)
    public ResponseEntity<Object> handleMonoException(MonoException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(ex.getMessage());
    }
}
