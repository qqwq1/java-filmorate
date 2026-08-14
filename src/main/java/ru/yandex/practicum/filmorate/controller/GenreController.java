package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;


import java.util.Collection;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<Collection<Genre>> findAllGenres() {
        return ResponseEntity.ok()
                .body(filmService.findAllGenres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Genre> findGenresById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(filmService.findGenreById(id));
    }
}
