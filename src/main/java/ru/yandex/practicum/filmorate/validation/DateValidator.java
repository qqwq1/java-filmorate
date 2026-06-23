package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateValidator implements ConstraintValidator<ReleaseAfter, LocalDate> {
    private LocalDate startDate;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Override
    public void initialize(ReleaseAfter constraintAnnotation) {
        startDate = LocalDate.parse(constraintAnnotation.date(), formatter);
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext constraintValidatorContext) {
        if (releaseDate != null) {
            return releaseDate.isAfter(startDate);
        }
        return true;
    }
}
