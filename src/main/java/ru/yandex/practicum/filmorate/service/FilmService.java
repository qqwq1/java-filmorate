package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film addFilm(Film filmToAdd) {
        return filmStorage.add(filmToAdd);
    }

    public Film updateFilm(Film film) {
        return filmStorage.update(film);
    }

    public Film getById(Long id) {
        return filmStorage.get(id);
    }

    public Film setLike(Long filmId, Long userId) {
        User user = userStorage.get(userId);
        Film film = filmStorage.get(filmId);

        user.setFilmLike(filmId);
        film.setUserLike(userId);

        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        User user = userStorage.get(userId);
        Film film = filmStorage.get(filmId);

        user.deleteFilmLike(filmId);
        film.deleteUserLike(userId);

        return film;
    }

    public List<Film> findTopMostLikedMovies(Integer limit) {
        return filmStorage.findTopMostLikedMovies(limit);
    }
}
