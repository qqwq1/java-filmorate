package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.groups.Create;
import ru.yandex.practicum.filmorate.validation.ReleaseAfter;
import ru.yandex.practicum.filmorate.groups.Update;


import java.time.LocalDate;

@Data
public class Film {
    private static final int MAX_DESCRIPTION_LENGTH = 200;
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым",
            groups = Create.class)
    private String name;

    @Size(max = 200,
            message = "Максимальная длина описания -> " + MAX_DESCRIPTION_LENGTH,
            groups = {Create.class, Update.class})
    private String description;

    @PastOrPresent(message = "Дата релиза должна быть раньше текущей даты",
            groups = {Create.class, Update.class})
    @ReleaseAfter(date = "28.12.1895",
            groups = {Create.class, Update.class})
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом",
            groups = {Create.class, Update.class})
    private Integer duration;

}
