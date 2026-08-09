package ru.yandex.practicum.filmorate.dal.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.CopyUtil;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> implements UserStorage {
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_ALL_USERS_QUERY = "SELECT * FROM users";
    private static final String INSERT_USER_QUERY = "INSERT INTO users(name, login, email, birthday)" +
            " VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET name = ?, login = ?, email = ?, birthday = ?" +
            " WHERE user_id = ?";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO user_friends(user_id, friend_id) VALUES (?, ?)";

    private static final String FIND_ALL_USER_FRIENDS_QUERY = """
            SELECT u.user_id, u.name, u.login, u.email, u.birthday
            FROM user_friends AS uf
            JOIN users AS u ON uf.friend_id = u.user_id
            WHERE uf.user_id = ?
            """;

    private static final String DELETE_USER_FRIEND_QUERY = "DELETE FROM user_friends WHERE user_id =? AND friend_id = ?";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Optional<User> get(Long id) {
        return findOne(FIND_USER_BY_ID_QUERY, id);
    }

    @Override
    public List<User> findAll() {
        return findAll(FIND_ALL_USERS_QUERY);
    }

    @Override
    public User delete(Long dataId) {
        return null;
    }

    @Override
    public User add(User user) {
        user.setId(save(INSERT_USER_QUERY, user.getName(), user.getLogin(), user.getEmail(), user.getBirthday()));
        return user;
    }

    @Override
    public User update(User updatedUser) {
        User userToUpdate = get(updatedUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", updatedUser.getId().toString()));
        CopyUtil.copyNonNullProperties(updatedUser, userToUpdate);

        update(UPDATE_USER_QUERY, userToUpdate.getName(), userToUpdate.getLogin(),
                userToUpdate.getEmail(), userToUpdate.getBirthday(), userToUpdate.getId());
        return userToUpdate;
    }

    @Override
    public List<User> findAllFriends(Long userId) {
        User user = get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", userId.toString()));
        return findAll(FIND_ALL_USER_FRIENDS_QUERY, userId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = get(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", userId.toString()));
        ;
        User friend = get(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден", friendId.toString()));
        ;

        delete(DELETE_USER_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public List<User> findAllCommonFriends(Long firstUserId, Long secondUserId) {
        List<User> firstUserFriends = findAllFriends(firstUserId);
        List<User> secondUserFriends = findAllFriends(secondUserId);

        return firstUserFriends.stream()
                .filter(secondUserFriends::contains)
                .toList();
    }

    public void addFriend(Long userID, Long friendId) {
        try {
            update(ADD_FRIEND_QUERY, userID, friendId);
        } catch (DataIntegrityViolationException e) {
            throw new NotFoundException("Пользователя с таким id не существует",
                    String.format("id = %d, id=%d", userID, friendId));
        }
    }
}
