package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.group.Create;
import ru.yandex.practicum.filmorate.validation.ReleaseAfter;
import ru.yandex.practicum.filmorate.group.Update;

import java.time.LocalDate;
import java.util.ArrayList;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class Film implements Identifiable {
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    @NotNull(message = "id фильма должен быть указан", groups = Update.class)
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым.", groups = Create.class)
    private String name;

    @Size(
            max = 200,
            message = "Максимальная длина описания -> " + MAX_DESCRIPTION_LENGTH + ".",
            groups = {Create.class, Update.class}
    )
    private String description;

    @PastOrPresent(message = "Дата релиза должна быть раньше текущей даты.", groups = {Create.class, Update.class})
    @ReleaseAfter(date = "28.12.1895", groups = {Create.class, Update.class})
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом.",
            groups = {Create.class, Update.class}
    )
    private Integer duration;

    @Builder.Default
    private List<Genre> genres = new ArrayList<>();

    private RatingMpa mpa;
}
