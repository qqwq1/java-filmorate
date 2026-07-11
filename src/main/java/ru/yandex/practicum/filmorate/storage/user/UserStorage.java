package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;


public interface UserStorage extends BaseStorage<User> {
    List<User> findAllFriends(Long id);

    List<User> findAllCommonFriends(Long id, Long otherId);
}
