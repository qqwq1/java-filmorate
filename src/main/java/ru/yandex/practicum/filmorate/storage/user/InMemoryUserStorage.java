package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;


@Component
@AllArgsConstructor
public class InMemoryUserStorage extends InMemoryStorage<User> implements UserStorage {

    @Override
    public List<User> findAllFriends(Long id) {
        User user = get(id);
        Set<Long> friendsIdSet = user.getFriendsIdSet();

        return friendsIdSet.stream()
                .map(super::get)
                .toList();
    }

    @Override
    public List<User> findAllCommonFriends(Long id, Long otherId) {
        Set<Long> firstUserFriendsIdSet = get(id).getFriendsIdSet();
        Set<Long> secondUserFriendsIdSet = get(otherId).getFriendsIdSet();

        return firstUserFriendsIdSet.stream()
                .filter(secondUserFriendsIdSet::contains)
                .map(super::get)
                .toList();
    }
}
