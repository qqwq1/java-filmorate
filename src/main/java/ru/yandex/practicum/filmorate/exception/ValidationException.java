package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final String rejectedValue;

    public ValidationException(String message, Object rejectedValue) {
        super(message);
        this.rejectedValue = rejectedValue.toString();
    }
}
