package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.groups.Create;
import ru.yandex.practicum.filmorate.validation.Empty;
import ru.yandex.practicum.filmorate.validation.ReleaseAfter;
import ru.yandex.practicum.filmorate.groups.Update;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film implements Identifiable {
    private static final int MAX_DESCRIPTION_LENGTH = 200;

    @NotNull(
            message = "id фильма должен быть указан",
            groups = Update.class
    )
    private Long id;

    @Empty(
            message = "Лайки пользователей можно добавлять только отдельным методом.",
            groups = {Create.class, Update.class}
    )
    private final Set<Long> usersLikesIdSet = new HashSet<>();

    @NotNull(
            message = "id не может быть пустым.",
            groups = Update.class
    )

    @NotBlank(
            message = "Название фильма не может быть пустым.",
            groups = Create.class
    )
    private String name;

    @Size(
            max = 200,
            message = "Максимальная длина описания -> " + MAX_DESCRIPTION_LENGTH + ".",
            groups = {Create.class, Update.class}
    )
    private String description;

    @PastOrPresent(
            message = "Дата релиза должна быть раньше текущей даты.",
            groups = {Create.class, Update.class}
    )
    @ReleaseAfter(
            date = "28.12.1895",
            groups = {Create.class, Update.class}
    )
    private LocalDate releaseDate;

    @Positive(
            message = "Продолжительность фильма должна быть положительным числом.",
            groups = {Create.class, Update.class}
    )
    private Integer duration;

    //private Integer likesCount;

    public void setUserLike(Long userId) {
        if (usersLikesIdSet.contains(userId)) {
            throw new ValidationException("Ошибка! Фильм с id=" + this.id + " уже отмечен данным пользователем",
                    userId);
        }
        usersLikesIdSet.add(userId);
    }

    public void deleteUserLike(Long userId) {
        if (!usersLikesIdSet.contains(userId)) {
            throw new ValidationException("Ошибка! Фильм с id=" + this.id + " не имеет лайка от данного пользователя",
                    userId);
        }
        usersLikesIdSet.add(userId);
    }

    public int getLikesCount() {
        return usersLikesIdSet.size();
    }
}
