package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final FilmService filmService;

    @GetMapping
    public ResponseEntity<Collection<RatingMpa>> findAllMpa() {
        return ResponseEntity.ok()
                .body(filmService.findAllRatings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingMpa> findMpaById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .body(filmService.findMpaById(id));
    }
}
