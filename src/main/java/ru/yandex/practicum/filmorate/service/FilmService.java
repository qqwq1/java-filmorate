package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.dal.repository.FilmRepository;
import ru.yandex.practicum.filmorate.dal.repository.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;


import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;


    public Collection<Film> findAll() {
        return filmRepository.findAll();
    }

    public Film addFilm(Film filmToAdd) {
        checkGenresAndMpa(filmToAdd);
        return filmRepository.add(filmToAdd);
    }

    private void checkGenresAndMpa(Film filmDto) {
        Set<Long> genreIdSet = filmRepository.findAllGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        Set<Long> mpaIdSet = filmRepository.findAllMpa().stream().map(RatingMpa::getId).collect(Collectors.toSet());
        if (filmDto.getMpa() != null && !mpaIdSet.contains(filmDto.getMpa().getId())) {
            throw new NotFoundException("Рейтинг с указанным id отсутствует", filmDto.getMpa().getId().toString());
        }
        if (filmDto.getGenres() != null) {
            filmDto.getGenres()
                    .forEach(genre -> {
                        if (!genreIdSet.contains(genre.getId())) {
                            throw new NotFoundException("Жанр с указанным id отсутствует",
                                    genre.getId().toString());
                        }
                    });
        }
    }

    public Film updateFilm(Film updatedFilm) {
        checkGenresAndMpa(updatedFilm);
        return filmRepository.update(updatedFilm);
    }

    public Film findById(Long id) {
        return filmRepository.get(id)
                .orElseThrow(() -> new NotFoundException("Фильм с указанным id отсутствует", id.toString()));
    }

    public void setLike(Long filmId, Long userId) {
        Film film = filmRepository.get(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с указанным id отсутствует", filmId.toString()));
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", userId.toString()));

        filmRepository.addLike(filmId, userId);
    }

    public void deleteLike(Long filmId, Long userId) {
        Film film = filmRepository.get(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с указанным id отсутствует", filmId.toString()));
        User user = userRepository.get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", userId.toString()));

        filmRepository.deleteLike(filmId, userId);
    }

    public List<Film> findTopMostLikedMovies(Integer limit) {
        return filmRepository.findTopMostLikedMovies(limit);
    }

    public List<Genre> findAllGenres() {
        return filmRepository.findAllGenres();
    }

    public List<RatingMpa> findAllRatings() {
        return filmRepository.findAllMpa();
    }

    public Genre findGenreById(Long id) {
        List<Long> genreIds = filmRepository.findAllGenres().stream()
                .map(Genre::getId)
                .toList();
        if (!genreIds.contains(id)) {
            throw new NotFoundException("Жанра с таким id не существует", id.toString());
        }
        return filmRepository.findGenreById(id);
    }

    public RatingMpa findMpaById(Long id) {
        List<Long> mpaIds = filmRepository.findAllMpa().stream()
                .map(RatingMpa::getId)
                .toList();
        if (!mpaIds.contains(id)) {
            throw new NotFoundException("Рейтинга с таким id не существует", id.toString());
        }
        return filmRepository.findRatingById(id);
    }
}
