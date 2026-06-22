package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Long, Film> filmsStorage = new HashMap<>();
    private final LocalDate START_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok()
                .body(filmsStorage.values());
    }

    @PostMapping
    public ResponseEntity<?> addFilm(@Valid @RequestBody Film filmToAdd) {
//        if (filmToAdd.getName().isBlank()) {
//            log.info("ValidationException\nПоле name в теле запроса пустое");
//            throw new ValidationException("Поле name в теле запроса пустое");
//        }
//        if (filmToAdd.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
//            log.info("ValidationException\nДлина поля description в теле запроса -> {}; MAX_DESCRIPTION_LENGTH -> {}",
//                    filmToAdd.getDescription().length(),
//                    MAX_DESCRIPTION_LENGTH);
//            throw new ValidationException(String.format("Длина описания превышает %s символов",
//                    MAX_DESCRIPTION_LENGTH));
//        }
//        if (filmToAdd.getDuration() <= 0) {
//            log.info("ValidationException\nЗначение поля duration < 0");
//            throw new ValidationException("Значение поля duration < 0");
//        }
        if (filmToAdd.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза не может быть пустой");
        }
        validateReleaseDate(filmToAdd);

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
    public ResponseEntity<?> updateFilm(@Valid @RequestBody Film filmToUpdate) {
        Film oldFilm = getFilmOrElseThrow(filmToUpdate.getId());
        if (filmToUpdate.getReleaseDate() != null) {
            validateReleaseDate(filmToUpdate);
        }
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

        log.info("Фильм с id: {} обновлен", oldFilm.getId());
        return ResponseEntity.ok()
                .body(oldFilm);
    }

    private Film getFilmOrElseThrow(Long filmId) {
        return Optional.ofNullable(filmsStorage.get(filmId))
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(START_DATE)) {
            throw new ValidationException("Дата релиза раньше " + START_DATE);
        }
    }
}
