package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.groups.Create;
import ru.yandex.practicum.filmorate.groups.Update;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
@AllArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public ResponseEntity<Collection<User>> findAll() {
        return ResponseEntity.ok()
                .body(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok()
                .body(service.getById(id));
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> findAllFriends(
            @PathVariable("id") Long id,
            @PathVariable("otherId") Long otherId) {
        return ResponseEntity.ok()
                .body(service.findAllCommonFriends(id, otherId));
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> findAllFriends(@PathVariable("id") Long id) {
        return ResponseEntity.ok()
                .body(service.findAllFriends(id));
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Validated(Create.class) @RequestBody User userToAdd) {
        User addedUser = service.addUser(userToAdd);

        log.info("Добавлен новый пользователь с id = {}", addedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addedUser);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Validated(Update.class) @RequestBody User updatedUser) {
        User newUser = service.updateUser(updatedUser);

        log.info("Пользователь с id: {} обновлен", newUser.getId());
        return ResponseEntity.ok()
                .body(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<List<User>> addFriend(
            @PathVariable("id") Long id,
            @PathVariable("friendId") Long friendId
    ) {
        log.info("Пользователь с id: {} добавлен в друзья к пользователю с id: {}", id, friendId);
        return ResponseEntity.ok()
                .body(service.addFriend(id, friendId));
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<List<User>> deleteFriend(
            @PathVariable("id") Long id,
            @PathVariable("friendId") Long friendId
    ) {
        log.info("Пользователь с id: {} удалил из друзей пользователя с id: {}", id, friendId);
        return ResponseEntity.ok()
                .body(service.deleteFriend(id, friendId));
    }
}
