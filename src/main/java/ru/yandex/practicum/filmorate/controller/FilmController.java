package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.group.Create;
import ru.yandex.practicum.filmorate.group.Update;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@AllArgsConstructor
@Slf4j
public class FilmController {
    private final FilmService service;

    @GetMapping
    public ResponseEntity<Collection<Film>> findAll() {
        return ResponseEntity.ok()
                .body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok()
                .body(service.findById(id));
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> findTopMostLikedMovies(
            @RequestParam(name = "count", required = false) Integer limit) {
        return ResponseEntity.ok(service.findTopMostLikedMovies(limit));
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Validated(value = Create.class) @RequestBody Film filmToAdd) {
        Film addedFilm = service.addFilm(filmToAdd);

        log.info("В хранилище добавлен новый фильм c id = {}", addedFilm.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addedFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Validated(value = Update.class) @RequestBody Film updatedFilm) {
        Film newFilm = service.updateFilm(updatedFilm);

        log.info("Фильм с id = {} обновлен", newFilm.getId());
        return ResponseEntity.ok()
                .body(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> likeFilm(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        service.setLike(filmId, userId);
        log.info("Пользователь с id: {} поставил лайк фильму с id: {}", userId, filmId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> deleteLike(
            @PathVariable("id") Long filmId,
            @PathVariable("userId") Long userId
    ) {
        service.deleteLike(filmId, userId);
        log.info("Пользователь с id: {} удалил лайк у фильма с id: {}", userId, filmId);
        return ResponseEntity.ok().build();
    }
}
