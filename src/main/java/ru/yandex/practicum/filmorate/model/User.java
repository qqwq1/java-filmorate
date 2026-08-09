package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import ru.yandex.practicum.filmorate.group.Create;
import ru.yandex.practicum.filmorate.group.Update;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@Builder
public class User implements Identifiable {
    @NotNull(message = "id пользователя должен быть указан", groups = Update.class)
    private Long id;

    private String name;

    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
            message = "Логин может содержать только латинские буквы, цифры, точки, дефисы и подчеркивания.",
            groups = {Create.class, Update.class}
    )
    @NotBlank(message = "Логин не может быть пустым.", groups = Create.class)
    private String login;

    @Email(message = "Email не соответствует формату", groups = {Create.class, Update.class})
    @NotBlank(message = "Электронная почта не может быть пустой", groups = Create.class)
    private String email;

    @PastOrPresent(message = "Дата рождения не может быть в будущем.", groups = {Create.class, Update.class})
    private LocalDate birthday;
}
