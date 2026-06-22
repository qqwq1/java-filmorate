package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;


import java.time.LocalDate;

@Data
public class Film {
    private final static int MAX_DESCRIPTION_LENGTH = 200;
    private Long id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания ->" + MAX_DESCRIPTION_LENGTH)
    private String description;

    @PastOrPresent(message = "Дата релиза должна быть раньше текущей даты")
    private LocalDate releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;

}
