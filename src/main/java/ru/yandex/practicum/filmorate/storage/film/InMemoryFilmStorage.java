package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;


@Component
@AllArgsConstructor
public class InMemoryFilmStorage extends InMemoryStorage<Film> implements FilmStorage {
    private static final int DEFAULT_TOP_FILMS_LIMIT = 10;

    @Override
    public List<Film> findTopMostLikedMovies(Integer limit) {
        int finalLimit = limit == null ? DEFAULT_TOP_FILMS_LIMIT : limit;
        return null;
    }
}
