package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private Long id;

    @Email(message = "Email не соответствует формату")
    private String email;

    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Логин может содержать только латинские буквы, цифры, точки, дефисы и подчеркивания"
    )
    private String login;
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
