package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(fieldError -> {
                    String fieldName = fieldError.getField();
                    String message = fieldError.getDefaultMessage();
                    errors.put(fieldName, message);
                    log.debug("Ошибка валидации для поля {}, значение \"{}\"\n сообщение: {}",
                            fieldName,
                            fieldError.getRejectedValue(),
                            message);
                });
        log.warn("Ошибка валидации: {}", errors);
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public Map<String, String> handleValidationException(ValidationException ex) {
        log.warn("Ошибка валидации: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, String> handleNotFoundException(NotFoundException ex) {
        log.warn("Ошибка при поиске по id: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, String> handleGeneralException(Exception ex) {
        log.error("Unexpected error", ex);
        return Map.of("error", "Внутренняя ошибка сервера");
    }
}
