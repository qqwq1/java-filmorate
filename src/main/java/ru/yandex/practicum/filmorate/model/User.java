package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.groups.Create;
import ru.yandex.practicum.filmorate.groups.Update;
import ru.yandex.practicum.filmorate.validation.Empty;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User implements Identifiable {
    @NotNull(
            message = "id пользователя должен быть указан",
            groups = Update.class
    )
    private Long id;

    @Empty(
            message = "Друзья пользователя добавляются отдельным методом.",
            groups = {Create.class, Update.class}
    )
    private Set<Long> friendsIdSet = new HashSet<>();

    @Empty(
            message = "Лайки для фильмов добавляются отдельным методом.",
            groups = {Create.class, Update.class}
    )
    private Set<Long> likedFilmIdSet = new HashSet<>();

    @Email(
            message = "Email не соответствует формату",
            groups = {Create.class, Update.class}
    )
    @NotBlank(
            message = "Электронная почта не может быть пустой",
            groups = Create.class
    )
    private String email;

    @Pattern(
            regexp = "^[a-zA-Z0-9._-]+$",
            message = "Логин может содержать только латинские буквы, цифры, точки, дефисы и подчеркивания.",
            groups = {Create.class, Update.class}
    )
    @NotBlank(
            message = "Логин не может быть пустым.",
            groups = Create.class
    )
    private String login;
    private String name;

    @PastOrPresent(
            message = "Дата рождения не может быть в будущем.",
            groups = {Create.class, Update.class}
    )
    private LocalDate birthday;

    public void addFriend(Long friendId) {
        if (friendId == null) {
            throw new ValidationException("id друга не может быть null.");
        }
        if (friendsIdSet.contains(friendId)) {
            throw new ValidationException("Ошибка! Пользователь с id=" + friendId + " уже добавлен в друзья.");
        }
        friendsIdSet.add(friendId);
    }

    public void deleteFriend(Long friendId) {
        if (friendId == null) {
            throw new ValidationException("id друга не может быть null.");
        }
        if (!friendsIdSet.contains(friendId)) {
            throw new ValidationException("Ошибка! Пользователь с id=" + friendId + " отсутствует в друзьях.");
        }
        friendsIdSet.remove(friendId);
    }

    public void setFilmLike(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("id фильма не может быть null");
        }
        if (likedFilmIdSet.contains(filmId)) {
            throw new ValidationException("Ошибка! Фильм с id=" + filmId + " уже отмечен данным пользователем");
        }
        likedFilmIdSet.add(filmId);
    }

    public void deleteFilmLike(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("id фильма не может быть null");
        }
        if (!likedFilmIdSet.contains(filmId)) {
            throw new ValidationException("Ошибка! Фильм с id=" + filmId + " не имеет лайка от данного пользователя");
        }
        likedFilmIdSet.remove(filmId);
    }
}
