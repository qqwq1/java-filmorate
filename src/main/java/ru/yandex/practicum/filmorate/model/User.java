package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.groups.Create;
import ru.yandex.practicum.filmorate.groups.Update;

import java.time.LocalDate;

@Data
public class User {
    private Long id;

    @Email(message = "Email не соответствует формату",
            groups = {Create.class, Update.class})
    @NotNull(message = "Электронная почта не может быть пустой",
            groups = Create.class)
    private String email;

    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Логин может содержать только латинские буквы, цифры, точки, дефисы и подчеркивания",
            groups = {Create.class, Update.class}
    )
    @NotNull(message = "Логин не может быть пустым",
            groups = Create.class)
    private String login;
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем",
            groups = {Create.class, Update.class})
    private LocalDate birthday;
}
