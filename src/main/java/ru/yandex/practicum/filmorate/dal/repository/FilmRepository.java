package ru.yandex.practicum.filmorate.dal.repository;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.CopyUtil;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Repository
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {

    private final RowMapper<Genre> genreRowMapper;
    private final RowMapper<RatingMpa> mpaRowMapper;

    private static final String FIND_FILM_BY_ID_QUERY =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, g.genre_id, g.name AS genre_name, " +
                    "rm.rating, rm.mpa_id " +
                    "FROM films f " +
                    "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN film_ratingMPA fr ON f.film_id=fr.film_id " +
                    "LEFT JOIN ratingMPA rm ON fr.mpa_id = rm.mpa_id " +
                    "WHERE f.film_id = ?";

    private static final String FIND_ALL_FILMS_QUERY =
            "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, g.genre_id, g.name AS genre_name, " +
                    "rm.rating, rm.mpa_id " +
                    "FROM films f " +
                    "LEFT JOIN film_genre fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genre g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN film_ratingMPA fr ON f.film_id=fr.film_id " +
                    "LEFT JOIN ratingMPA rm ON fr.mpa_id = rm.mpa_id";
    private static final String INSERT_FILM_QUERY = "INSERT INTO films(name, description, release_date, duration) VALUES (?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = """
            UPDATE films SET name = ?, description = ?, release_date = ?,
            duration = ?
            WHERE film_id = ?
            """;
    private static final String INSERT_FILM_GENRE_QUERY = """
            INSERT INTO film_genre (film_id, genre_id)
            SELECT ?, ?
            WHERE NOT EXISTS (
                SELECT 1
                FROM film_genre
                WHERE film_id = ?
                  AND genre_id = ?
            )
            """;
    private static final String INSERT_FILM_RATING_QUERY = "INSERT INTO film_ratingMPA(film_id, mpa_id) VALUES (?, ?)";
    private static final String FIND_ALL_GENRES = "SELECT * FROM genre";
    private static final String FIND_GENRE_BY_ID = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String FIND_ALL_RATINGS = "SELECT * FROM ratingMPA";
    private static final String FIND_RATING_BY_ID = "SELECT * FROM ratingMPA WHERE mpa_id = ?";
    private static final String DELETE_ALL_FILM_GENRE_QUERY = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String UPDATE_FILM_RATING_QUERY = "UPDATE film_ratingMPA SET mpa_id = ? WHERE film_id = ?";
    private static final String SET_FILM_USER_LIKE = "INSERT INTO film_user_like(user_id,film_id) VALUES(?,?)";
    private static final String DELETE_FILM_USER_LIKE = "DELETE FROM film_user_like WHERE user_id=? AND film_id=?";
    private static final String FIND_MOST_POPULAR_FILM_ID = """
            SELECT film_id, COUNT(user_id) AS likes_count
            FROM film_user_like
            GROUP BY film_id
            ORDER BY likes_count DESC
            LIMIT ?
            """;

    private static final Integer DEFAULT_TOP_FILMS_LIMIT = 10;

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper,
                          RowMapper<Genre> genreRowMapper, RowMapper<RatingMpa> mpaRowMapper) {
        super(jdbc, mapper);
        this.genreRowMapper = genreRowMapper;
        this.mpaRowMapper = mpaRowMapper;
    }

    @Override
    public Optional<Film> get(Long id) {

        Film filmFound = jdbc.query(FIND_FILM_BY_ID_QUERY, rs -> {
            Film film = Film.builder().build();
            while (rs.next()) {
                if (film.getId() == null) {
                    film = createFilm(rs);
                }
                if (rs.getLong("genre_id") != 0) {
                    film.getGenres().add(createGenre(rs));
                }
            }

            return film;
        }, id);
        assert filmFound != null;
        if (filmFound.getId() == null) {
            filmFound = null;
        }
        return Optional.ofNullable(filmFound);
    }

    @Override
    public List<Film> findAll() {
        return jdbc.query(FIND_ALL_FILMS_QUERY, rs -> {
            Map<Long, Film> films = new HashMap<>();

            while (rs.next()) {

                long filmId = rs.getLong("film_id");

                Film film = films.get(filmId);

                if (film == null) {
                    film = createFilm(rs);
                    films.put(filmId, film);
                }

                film.getGenres().add(createGenre(rs));
            }

            return new ArrayList<>(films.values());
        });
    }

    @Override
    public Film delete(Long dataId) {
        return null;
    }

    @Transactional
    @Override
    public Film add(Film film) {
        film.setId(save(INSERT_FILM_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration()));

        if (film.getGenres() != null) {
            addFilmGenres(film.getId(), film.getGenres());
        }
        if (film.getMpa() != null) {
            update(INSERT_FILM_RATING_QUERY, film.getId(), film.getMpa().getId());
        }
        return film;
    }

    @Transactional
    @Override
    public Film update(Film updatedFilm) {

        Film filmToUpdate = get(updatedFilm.getId())
                .orElseThrow(
                        () -> new NotFoundException("Фильм с указанным id отсутствует", updatedFilm.getId().toString()));
        CopyUtil.copyNonNullProperties(updatedFilm, filmToUpdate);

        update(UPDATE_FILM_QUERY, filmToUpdate.getName(), filmToUpdate.getDescription(), filmToUpdate.getReleaseDate(),
                filmToUpdate.getDuration(), filmToUpdate.getId());
        delete(DELETE_ALL_FILM_GENRE_QUERY, filmToUpdate.getId());
        addFilmGenres(filmToUpdate.getId(), filmToUpdate.getGenres());
        update(UPDATE_FILM_RATING_QUERY, filmToUpdate.getMpa().getId(), filmToUpdate.getId());

        return filmToUpdate;
    }

    @Override
    public List<Film> findTopMostLikedMovies(Integer limit) {
        Integer finalLimit = limit == null ? DEFAULT_TOP_FILMS_LIMIT : limit;

        return Objects.requireNonNull(jdbc.query(FIND_MOST_POPULAR_FILM_ID, rs -> {
                    List<Long> filmIds = new ArrayList<>();
                    while (rs.next()) {
                        filmIds.add(rs.getLong("film_id"));
                    }
                    return filmIds;
                }, finalLimit)).stream()
                .map(this::get)
                .map(Optional::orElseThrow)
                .toList();
    }

    private Film createFilm(ResultSet rs) throws SQLException {
        return Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(
                        RatingMpa.builder()
                                .id(rs.getLong("mpa_id"))
                                .name(rs.getString("rating"))
                                .build()
                )
                .build();
    }

    private Genre createGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    private void addFilmGenres(Long filmId, List<Genre> genresList) {
        jdbc.batchUpdate(INSERT_FILM_GENRE_QUERY, genresList, genresList.size(),
                (ps, genre) -> {
                    ps.setLong(1, filmId);
                    ps.setLong(2, genre.getId());
                    ps.setLong(3, filmId);
                    ps.setLong(4, genre.getId());
                }
        );
    }

    public List<Genre> findAllGenres() {
        return jdbc.query(FIND_ALL_GENRES, genreRowMapper);
    }

    public List<RatingMpa> findAllMpa() {
        return jdbc.query(FIND_ALL_RATINGS, mpaRowMapper);
    }

    public void addLike(Long filmId, Long userId) {
        jdbc.update(SET_FILM_USER_LIKE, userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        jdbc.update(DELETE_FILM_USER_LIKE, userId, filmId);
    }


    public Genre findGenreById(Long genreId) {
        return jdbc.queryForObject(FIND_GENRE_BY_ID, genreRowMapper, genreId);
    }

    public RatingMpa findRatingById(Long ratingId) {
        return jdbc.queryForObject(FIND_RATING_BY_ID, mpaRowMapper, ratingId);
    }
}
