package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repository.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository repository;

    public Collection<User> findAll() {
        return repository.findAll();
    }

    public User addUser(User userToAdd) {
        Optional<String> userName = Optional.ofNullable(userToAdd.getName());
        if (userName.orElse("").isBlank()) {
            userToAdd.setName(userToAdd.getLogin());
        }

        return repository.add(userToAdd);
    }

    public User updateUser(User user) {
        return repository.update(user);
    }

    public User getById(Long id) {
        return repository.get(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден", id.toString()));
    }

    public void addFriend(Long userId, Long friendId) {
        repository.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        repository.deleteFriend(userId, friendId);
    }

    public List<User> findAllFriends(Long id) {
        return repository.findAllFriends(id);
    }

    public List<User> findAllCommonFriends(Long firstUserId, Long secondUserId) {
        return repository.findAllCommonFriends(firstUserId, secondUserId);
    }
}
