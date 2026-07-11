package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.GlobalExceptionHandler;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@Import(GlobalExceptionHandler.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FilmService service;

    private Film sampleFilm(Long id) {
        Film film = new Film();
        film.setId(id);
        film.setName("Inception");
        film.setDescription("A sci-fi thriller");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(148);
        return film;
    }

    @Test
    void addFilmSuccess() throws Exception {
        Film toAdd = sampleFilm(null);
        Film saved = sampleFilm(1L);
        when(service.addFilm(any(Film.class))).thenReturn(saved);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toAdd)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Inception")));
    }

    @Test
    void addFilmValidationFailsBlankName() throws Exception {
        Film invalid = sampleFilm(null);
        invalid.setName("");

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("name")));
    }

    @Test
    void getAllFilmsReturnsData() throws Exception {
        when(service.findAll()).thenReturn(List.of(sampleFilm(1L)));

        mockMvc.perform(get("/films").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void getFilmByIdReturnsData() throws Exception {
        when(service.getById(1L)).thenReturn(sampleFilm(1L));

        mockMvc.perform(get("/films/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Inception")));
    }

    @Test
    void getPopularFilmsReturnsData() throws Exception {
        when(service.findTopMostLikedMovies(5)).thenReturn(List.of(sampleFilm(1L), sampleFilm(2L)));

        mockMvc.perform(get("/films/popular").param("count", "5").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void updateFilmSuccess() throws Exception {
        Film update = sampleFilm(1L);
        update.setName("Updated");
        when(service.updateFilm(any(Film.class))).thenReturn(update);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Updated")));
    }

    @Test
    void updateFilmNotFound() throws Exception {
        Film update = sampleFilm(999L);
        when(service.updateFilm(any(Film.class)))
                .thenThrow(new NotFoundException("Фильм не найден", "999"));

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.field", is("id")));
    }

    @Test
    void likeFilmReturnsUpdatedFilm() throws Exception {
        Film liked = sampleFilm(1L);
        when(service.setLike(1L, 10L)).thenReturn(liked);

        mockMvc.perform(put("/films/{id}/like/{userId}", 1L, 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteLikeReturnsUpdatedFilm() throws Exception {
        Film withoutLike = sampleFilm(1L);
        when(service.deleteLike(1L, 10L)).thenReturn(withoutLike);

        mockMvc.perform(delete("/films/{id}/like/{userId}", 1L, 10L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }
}
