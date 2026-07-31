package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage extends BaseStorage<Film> {
    List<Film> findTopMostLikedMovies(Integer limit);
}
