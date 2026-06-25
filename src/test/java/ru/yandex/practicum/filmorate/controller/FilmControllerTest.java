package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ru.yandex.practicum.filmorate.controller.FilmController filmController;

    private Film validFilm;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(filmController, "filmsStorage", new java.util.HashMap<Long, Film>());
        validFilm = new Film();
        validFilm.setName("Inception");
        validFilm.setDescription("A sci-fi thriller");
        validFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        validFilm.setDuration(148);
    }

    @Test
    void testAddFilmSuccess() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Inception")))
                .andExpect(jsonPath("$.description", is("A sci-fi thriller")))
                .andExpect(jsonPath("$.duration", is(148)));
    }

    @Test
    void testAddFilmWithBlankName() throws Exception {
        validFilm.setName("");
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("name")))
                .andExpect(jsonPath("$[0].rejectedValue", is("")))
                .andExpect(jsonPath("$[0].description",notNullValue()));

    }

    @Test
    void testAddFilmWithNullName() throws Exception {
        validFilm.setName(null);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("name")))
                .andExpect(jsonPath("$[0].rejectedValue", is("null")))
                .andExpect(jsonPath("$[0].description", notNullValue()));
    }

    @Test
    void testAddFilmWithDescriptionTooLong() throws Exception {
        validFilm.setDescription("a".repeat(201));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("description")))
                .andExpect(jsonPath("$[0].description", notNullValue()));
    }

    @Test
    void testAddFilmWithNegativeDuration() throws Exception {
        validFilm.setDuration(-5);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("duration")));
    }

    @Test
    void testAddFilmWithZeroDuration() throws Exception {
        validFilm.setDuration(0);
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("duration")));
    }

    @Test
    void testAddFilmWithReleaseDateBeforeMinDate() throws Exception {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", containsString("releaseDate")));
    }

    @Test
    void testAddFilmWithReleaseDateInFuture() throws Exception {
        validFilm.setReleaseDate(LocalDate.now().plusYears(1));
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("releaseDate")));
    }

    @Test
    void testGetAllFilmsEmpty() throws Exception {
        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andDo(print());
    }

    @Test
    void testGetAllFilmsWithData() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/films")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Inception")));
    }

    @Test
    void testUpdateFilmSuccess() throws Exception {
        var result = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated())
                .andReturn();

        long filmId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        Film updateFilm = new Film();
        updateFilm.setId(filmId);
        updateFilm.setName("Inception Updated");
        updateFilm.setDescription("Updated description");
        updateFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        updateFilm.setDuration(150);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) filmId)))
                .andExpect(jsonPath("$.name", is("Inception Updated")))
                .andExpect(jsonPath("$.duration", is(150)));
    }

    @Test
    void testUpdateNonExistentFilm() throws Exception {
        Film updateFilm = new Film();
        updateFilm.setId(999L);
        updateFilm.setName("Non-existent");
        updateFilm.setDescription("Test");
        updateFilm.setReleaseDate(LocalDate.of(2010, 7, 16));
        updateFilm.setDuration(100);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateFilm)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.description", containsString("не найден")));
    }

    @Test
    void testUpdateFilmPartially() throws Exception {
        var result = mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validFilm)))
                .andExpect(status().isCreated())
                .andReturn();

        long filmId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        Film updateFilm = new Film();
        updateFilm.setId(filmId);
        updateFilm.setName("Only Name Changed");
        updateFilm.setDescription(null);
        updateFilm.setReleaseDate(null);
        updateFilm.setDuration(null);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateFilm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Only Name Changed")))
                .andExpect(jsonPath("$.description", is("A sci-fi thriller")))
                .andExpect(jsonPath("$.duration", is(148)));
    }
}