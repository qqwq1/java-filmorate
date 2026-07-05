package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String rejectedValue;

    public NotFoundException(String message, String rejectedValue) {
        super(message);
        this.rejectedValue = rejectedValue;
    }
}
