package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.groups.Create;
import ru.yandex.practicum.filmorate.groups.Update;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> filmsStorage = new HashMap<>();

    @GetMapping
    public ResponseEntity<Collection<Film>> findAll() {
        return ResponseEntity.ok()
                .body(filmsStorage.values());
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Validated(value = Create.class) @RequestBody Film filmToAdd) {
        filmToAdd.setId(getNextId());
        filmsStorage.put(filmToAdd.getId(), filmToAdd);
        log.info("В хранилище добавлен новый фильм c id = {}", filmToAdd.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(filmToAdd);

    }

    private long getNextId() {
        long currentMaxId = filmsStorage.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Validated(value = Update.class) @RequestBody Film filmToUpdate) {
        Film oldFilm = getFilmOrElseThrow(filmToUpdate.getId());

        if (filmToUpdate.getName() != null) {
            oldFilm.setName(filmToUpdate.getName());
        }
        if (filmToUpdate.getDescription() != null) {
            oldFilm.setDescription(filmToUpdate.getDescription());
        }
        if (filmToUpdate.getReleaseDate() != null) {
            oldFilm.setReleaseDate(filmToUpdate.getReleaseDate());
        }
        if (filmToUpdate.getDuration() != null) {
            oldFilm.setDuration(filmToUpdate.getDuration());
        }

        log.info("Фильм с id = {} обновлен", oldFilm.getId());
        return ResponseEntity.ok()
                .body(oldFilm);
    }

    private Film getFilmOrElseThrow(Long filmId) {
        return Optional.ofNullable(filmsStorage.get(filmId))
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден", filmId.toString()));
    }
}
