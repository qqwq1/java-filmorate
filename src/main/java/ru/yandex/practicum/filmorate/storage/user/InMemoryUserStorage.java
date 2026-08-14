package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
@AllArgsConstructor
public class InMemoryUserStorage extends InMemoryStorage<User> implements UserStorage {

    @Override
    public List<User> findAllFriends(Long id) {
        return null;
    }

    @Override
    public List<User> findAllCommonFriends(Long id, Long otherId) {
        return null;
    }
}