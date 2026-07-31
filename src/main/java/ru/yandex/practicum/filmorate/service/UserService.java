package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User addUser(User userToAdd) {
        Optional<String> userName = Optional.ofNullable(userToAdd.getName());
        if (userName.orElse("").isBlank()) {
            userToAdd.setName(userToAdd.getLogin());
        }

        return userStorage.add(userToAdd);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public User getById(Long id) {
        return userStorage.get(id);
    }

    public List<User> addFriend(Long id, Long friendId) {
        User user = userStorage.get(id);
        User friend = userStorage.get(friendId);

        user.addFriend(friendId);
        friend.addFriend(id);

        return List.of(user, friend);
    }

    public List<User> deleteFriend(Long id, Long friendId) {
        User user = userStorage.get(id);
        User friend = userStorage.get(friendId);

        user.deleteFriend(friendId);
        friend.deleteFriend(id);

        return List.of(user, friend);
    }

    public List<User> findAllFriends(Long id) {
        return userStorage.findAllFriends(id);
    }

    public List<User> findAllCommonFriends(Long id, Long otherId) {
        return userStorage.findAllCommonFriends(id, otherId);
    }
}
