package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.groups.Create;
import ru.yandex.practicum.filmorate.groups.Update;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public ResponseEntity<Collection<User>> findAll() {
        return ResponseEntity.ok()
                .body(users.values());
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Validated(Create.class) @RequestBody User userToAdd) {

        Optional<String> userName = Optional.ofNullable(userToAdd.getName());
        if (userName.orElse("").isBlank()) {
            userToAdd.setName(userToAdd.getLogin());
        }

        userToAdd.setId(getNextId());
        users.put(userToAdd.getId(), userToAdd);
        log.info("Добавлен новый пользователь с id = {}", userToAdd.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userToAdd);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Validated(Update.class) @RequestBody User userToUpdate) {
        User oldUser = getUserOrElseThrow(userToUpdate.getId());

        if (userToUpdate.getEmail() != null) {
            oldUser.setEmail(userToUpdate.getEmail());
        }
        if (userToUpdate.getLogin() != null) {
            oldUser.setLogin(userToUpdate.getLogin());
        }
        if (userToUpdate.getName() != null) {
            oldUser.setName(userToUpdate.getName());
        }
        if (userToUpdate.getBirthday() != null) {
            oldUser.setBirthday(userToUpdate.getBirthday());
        }

        log.info("Пользователь с id: {} обновлен", oldUser.getId());
        return ResponseEntity.ok()
                .body(oldUser);
    }

    private User getUserOrElseThrow(Long userId) {
        return Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден",
                        userId.toString()));
    }
}
