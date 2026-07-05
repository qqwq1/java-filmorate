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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserController userController;

    private User validUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userController, "users", new java.util.HashMap<Long, Film>());
        validUser = new User();
        validUser.setEmail("user@example.com");
        validUser.setLogin("userlogin");
        validUser.setName("User Name");
        validUser.setBirthday(LocalDate.of(1990, 5, 15));
    }

    @Test
    void testAddUserSuccess() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.email", is("user@example.com")))
                .andExpect(jsonPath("$.login", is("userlogin")))
                .andExpect(jsonPath("$.name", is("User Name")));
    }

    @Test
    void testAddUserWithoutNameUsesLogin() throws Exception {
        validUser.setName(null);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("userlogin")));
    }

    @Test
    void testAddUserWithBlankNameUsesLogin() throws Exception {
        validUser.setName("  ");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("userlogin")));
    }

    @Test
    void testAddUserWithInvalidEmail() throws Exception {
        validUser.setEmail("invalid-email");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("email")))
                .andExpect(jsonPath("$[0].description",notNullValue()));
    }

    @Test
    void testAddUserWithBlankEmail() throws Exception {
        validUser.setEmail("");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddUserWithInvalidLogin() throws Exception {
        validUser.setLogin("invalid login with spaces");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("login")))
                .andExpect(jsonPath("$[0].description",notNullValue()));
    }

    @Test
    void testAddUserWithBlankLogin() throws Exception {
        validUser.setLogin("");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddUserWithBirthdayInFuture() throws Exception {
        validUser.setBirthday(LocalDate.now().plusYears(1));
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("birthday")))
                .andExpect(jsonPath("$[0].description",notNullValue()));
    }

    @Test
    void testAddUserWithValidLogin() throws Exception {
        validUser.setLogin("valid_login-123");
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login", is("valid_login-123")));
    }

    @Test
    void testGetAllUsersEmpty() throws Exception {
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetAllUsersWithData() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is("user@example.com")));
    }

    @Test
    void testUpdateUserSuccess() throws Exception {
        var result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andReturn();

        long userId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setEmail("newemail@example.com");
        updateUser.setLogin("newlogin");
        updateUser.setName("New Name");
        updateUser.setBirthday(LocalDate.of(1985, 3, 20));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) userId)))
                .andExpect(jsonPath("$.email", is("newemail@example.com")))
                .andExpect(jsonPath("$.login", is("newlogin")))
                .andExpect(jsonPath("$.name", is("New Name")));
    }

    @Test
    void testUpdateNonExistentUser() throws Exception {
        User updateUser = new User();
        updateUser.setId(999L);
        updateUser.setEmail("test@example.com");
        updateUser.setLogin("testlogin");
        updateUser.setName("Test");
        updateUser.setBirthday(LocalDate.of(1990, 5, 15));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.field", is("id")))
                .andExpect(jsonPath("$.description",containsString("не найден")));
    }

    @Test
    void testUpdateUserPartially() throws Exception {
        var result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andReturn();

        long userId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setEmail("newemail@example.com");
        updateUser.setLogin(null);
        updateUser.setName(null);
        updateUser.setBirthday(null);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("newemail@example.com")))
                .andExpect(jsonPath("$.login", is("userlogin")))
                .andExpect(jsonPath("$.name", is("User Name")));
    }

    @Test
    void testUpdateUserWithInvalidEmail() throws Exception {
        var result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validUser)))
                .andExpect(status().isCreated())
                .andReturn();

        long userId = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setEmail("invalid-email");
        updateUser.setLogin("newlogin");
        updateUser.setName("New Name");
        updateUser.setBirthday(LocalDate.of(1985, 3, 20));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("email")))
                .andExpect(jsonPath("$[0].description",notNullValue()));
    }
}