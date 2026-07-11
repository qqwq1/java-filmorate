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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

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

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService service;

    private User sampleUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user@example.com");
        user.setLogin("userlogin");
        user.setName("User Name");
        user.setBirthday(LocalDate.of(1990, 5, 15));
        return user;
    }

    @Test
    void addUserSuccess() throws Exception {
        User toAdd = sampleUser(null);
        User saved = sampleUser(1L);
        when(service.addUser(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toAdd)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("user@example.com")));
    }

    @Test
    void addUserValidationFailsInvalidEmail() throws Exception {
        User invalid = sampleUser(null);
        invalid.setEmail("invalid-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field", is("email")));
    }

    @Test
    void getAllUsersReturnsData() throws Exception {
        when(service.findAll()).thenReturn(List.of(sampleUser(1L)));

        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)));
    }

    @Test
    void getUserByIdReturnsUser() throws Exception {
        when(service.getById(1L)).thenReturn(sampleUser(1L));

        mockMvc.perform(get("/users/{id}", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.login", is("userlogin")));
    }

    @Test
    void getCommonFriendsReturnsData() throws Exception {
        User friend = sampleUser(3L);
        when(service.findAllCommonFriends(1L, 2L)).thenReturn(List.of(friend));

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(3)));
    }

    @Test
    void getFriendsReturnsData() throws Exception {
        User friend = sampleUser(2L);
        when(service.findAllFriends(1L)).thenReturn(List.of(friend));

        mockMvc.perform(get("/users/{id}/friends", 1L).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)));
    }

    @Test
    void updateUserSuccess() throws Exception {
        User update = sampleUser(1L);
        update.setEmail("newemail@example.com");
        when(service.updateUser(any(User.class))).thenReturn(update);

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("newemail@example.com")));
    }

    @Test
    void updateUserNotFound() throws Exception {
        User update = sampleUser(999L);
        when(service.updateUser(any(User.class)))
                .thenThrow(new NotFoundException("Пользователь не найден", "999"));

        mockMvc.perform(put("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.field", is("id")));
    }

    @Test
    void addFriendReturnsUpdatedFriends() throws Exception {
        User friend = sampleUser(2L);
        when(service.addFriend(1L, 2L)).thenReturn(List.of(friend));

        mockMvc.perform(put("/users/{id}/friends/{friendId}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)));
    }

    @Test
    void deleteFriendReturnsUpdatedFriends() throws Exception {
        when(service.deleteFriend(1L, 2L)).thenReturn(List.of());

        mockMvc.perform(delete("/users/{id}/friends/{friendId}", 1L, 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
