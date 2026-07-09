package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

public class EmptyValidator implements ConstraintValidator<Empty, Collection<?>> {

    @Override
    public void initialize(Empty constraintAnnotation) {
    }

    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return value.isEmpty();
    }
}
