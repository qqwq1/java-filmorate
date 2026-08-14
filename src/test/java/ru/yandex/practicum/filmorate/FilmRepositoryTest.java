package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.dal.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.dal.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.dal.repository.FilmRepository;
import ru.yandex.practicum.filmorate.dal.repository.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        FilmRepository.class,
        UserRepository.class,
        FilmRowMapper.class,
        GenreRowMapper.class,
        MpaRowMapper.class,
        UserRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmRepositoryTest {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;

    private Film addFilm() {
        return filmRepository.add(Film.builder()
                .name("Фильм")
                .description("Описание фильма")
                .releaseDate(LocalDate.parse("2000-01-01"))
                .duration(120)
                .genres(List.of(Genre.builder().id(1L).build()))
                .mpa(RatingMpa.builder().id(1L).build())
                .build());
    }

    private Film addFilm(String name, String description, LocalDate releaseDate, Integer duration,
                         List<Genre> genres, Long mpaId) {
        return filmRepository.add(Film.builder()
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .genres(genres)
                .mpa(RatingMpa.builder().id(mpaId).build())
                .build());
    }

    private User addUser(String login, String email) {
        return userRepository.add(User.builder()
                .name("Пользователь")
                .login(login)
                .email(email)
                .birthday(LocalDate.parse("1990-01-01"))
                .build());
    }

    @Test
    public void testAddFilm() {
        Film film = addFilm();

        Assertions.assertNotNull(film.getId());
        Assertions.assertNotNull(film.getName());
        Assertions.assertNotNull(film.getDescription());
        Assertions.assertNotNull(film.getReleaseDate());
        Assertions.assertNotNull(film.getDuration());
        Assertions.assertNotNull(film.getMpa());
    }

    @Test
    public void testFindFilmById() {
        Film created = addFilm();
        Optional<Film> filmOptional = filmRepository.get(created.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", created.getId())
                );
    }

    @Test
    public void testFindAllFilms() {
        Film firstFilm = addFilm();
        Film secondFilm = addFilm(
                "Фильм 2",
                "Описание 2",
                LocalDate.parse("2001-02-02"),
                90,
                List.of(Genre.builder().id(2L).build()),
                2L
        );

        List<Film> films = filmRepository.findAll();

        assertThat(films)
                .extracting(Film::getId)
                .contains(firstFilm.getId(), secondFilm.getId());
    }

    @Test
    public void testUpdateFilm() {
        Film created = addFilm();
        Film updated = Film.builder()
                .id(created.getId())
                .name("Новое название")
                .description("Новое описание")
                .releaseDate(LocalDate.parse("2002-02-02"))
                .duration(140)
                .genres(List.of(Genre.builder().id(3L).build()))
                .mpa(RatingMpa.builder().id(3L).build())
                .build();

        Film result = filmRepository.update(updated);
        Optional<Film> filmFromDb = filmRepository.get(created.getId());

        assertThat(result.getName()).isEqualTo("Новое название");
        assertThat(result.getDescription()).isEqualTo("Новое описание");
        assertThat(result.getMpa().getId()).isEqualTo(3L);
        assertThat(filmFromDb)
                .isPresent()
                .hasValueSatisfying(film -> {
                    assertThat(film.getName()).isEqualTo("Новое название");
                    assertThat(film.getDescription()).isEqualTo("Новое описание");
                    assertThat(film.getMpa().getId()).isEqualTo(3L);
                    assertThat(film.getGenres())
                            .extracting(Genre::getId)
                            .containsExactly(3L);
                });
    }

    @Test
    public void testUpdateFilmWhenNotFound() {
        Film film = Film.builder()
                .id(99999L)
                .name("Нет в БД")
                .build();

        assertThatThrownBy(() -> filmRepository.update(film))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void testFindAllGenres() {
        List<Genre> genres = filmRepository.findAllGenres();

        assertThat(genres)
                .extracting(Genre::getName)
                .contains("Комедия", "Драма", "Боевик");
    }

    @Test
    public void testFindAllMpa() {
        List<RatingMpa> ratings = filmRepository.findAllMpa();

        assertThat(ratings)
                .extracting(RatingMpa::getName)
                .contains("G", "PG", "R");
    }

    @Test
    public void testFindGenreById() {
        Genre genre = filmRepository.findGenreById(1L);

        assertThat(genre.getId()).isEqualTo(1L);
        assertThat(genre.getName()).isEqualTo("Комедия");
    }

    @Test
    public void testFindRatingById() {
        RatingMpa rating = filmRepository.findRatingById(1L);

        assertThat(rating.getId()).isEqualTo(1L);
        assertThat(rating.getName()).isEqualTo("G");
    }

    @Test
    public void testFindTopMostLikedMovies() {
        Film firstFilm = addFilm();
        Film secondFilm = addFilm(
                "Второй фильм",
                "Описание второго",
                LocalDate.parse("2003-03-03"),
                100,
                List.of(Genre.builder().id(2L).build()),
                2L
        );
        User firstUser = addUser("first-user", "first-user@yandex.ru");
        User secondUser = addUser("second-user", "second-user@yandex.ru");

        filmRepository.addLike(firstFilm.getId(), firstUser.getId());
        filmRepository.addLike(firstFilm.getId(), secondUser.getId());
        filmRepository.addLike(secondFilm.getId(), firstUser.getId());

        List<Film> topFilms = filmRepository.findTopMostLikedMovies(2);

        assertThat(topFilms)
                .extracting(Film::getId)
                .containsExactly(firstFilm.getId(), secondFilm.getId());
    }

    @Test
    public void testDeleteLike() {
        Film film = addFilm();
        User user = addUser("user-with-like", "user-with-like@yandex.ru");
        filmRepository.addLike(film.getId(), user.getId());

        filmRepository.deleteLike(film.getId(), user.getId());

        assertThat(filmRepository.findTopMostLikedMovies(10)).isEmpty();
    }
}
