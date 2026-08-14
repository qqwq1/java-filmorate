package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.dal.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.dal.repository.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@Import({
        UserRepository.class,
        UserRowMapper.class
})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserRepositoryTest {
    private final UserRepository userRepository;

    private User addUser() {
        return userRepository.add(User.builder()
                .name("Иван")
                .login("Leshchev")
                .email("Leshchev.Ivan@yandex.ru")
                .birthday(LocalDate.parse("1946-08-20"))
                .build());
    }

    private User addUser(String name, String login, String email, LocalDate birthday) {
        return userRepository.add(User.builder()
                .name(name)
                .login(login)
                .email(email)
                .birthday(birthday)
                .build());
    }

    @Test
    public void testAddUser() {
        User user = addUser();

        Assertions.assertNotNull(user.getId());
        Assertions.assertNotNull(user.getLogin());
        Assertions.assertNotNull(user.getEmail());
        Assertions.assertNotNull(user.getBirthday());
    }

    @Test
    public void testFindUserById() {
        User created = addUser();
        Optional<User> userOptional = userRepository.get(created.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", created.getId())
                );
    }

    @Test
    public void testFindAllUsers() {
        User firstUser = addUser();
        User secondUser = addUser("Петр", "petya", "petya@yandex.ru", LocalDate.parse("2000-01-01"));

        List<User> users = userRepository.findAll();

        assertThat(users)
                .extracting(User::getId)
                .contains(firstUser.getId(), secondUser.getId());
    }

    @Test
    public void testUpdateUser() {
        User created = addUser();
        User updated = User.builder()
                .id(created.getId())
                .name("Новое имя")
                .build();

        User result = userRepository.update(updated);
        Optional<User> userFromDb = userRepository.get(created.getId());

        assertThat(result.getName()).isEqualTo("Новое имя");
        assertThat(result.getLogin()).isEqualTo(created.getLogin());
        assertThat(userFromDb)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getName()).isEqualTo("Новое имя");
                    assertThat(user.getLogin()).isEqualTo(created.getLogin());
                });
    }

    @Test
    public void testUpdateUserWhenNotFound() {
        User user = User.builder()
                .id(99999L)
                .name("Нет в БД")
                .build();

        assertThatThrownBy(() -> userRepository.update(user))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void testAddFriendAndFindAllFriends() {
        User user = addUser();
        User friend = addUser("Петр", "petya", "petya@yandex.ru", LocalDate.parse("2000-01-01"));
        userRepository.addFriend(user.getId(), friend.getId());

        List<User> friends = userRepository.findAllFriends(user.getId());

        assertThat(friends)
                .hasSize(1)
                .first()
                .extracting(User::getId)
                .isEqualTo(friend.getId());
    }

    @Test
    public void testDeleteFriend() {
        User user = addUser();
        User friend = addUser("Петр", "petya", "petya@yandex.ru", LocalDate.parse("2000-01-01"));
        userRepository.addFriend(user.getId(), friend.getId());

        userRepository.deleteFriend(user.getId(), friend.getId());

        assertThat(userRepository.findAllFriends(user.getId())).isEmpty();
    }

    @Test
    public void testFindAllCommonFriends() {
        User firstUser = addUser();
        User secondUser = addUser("Петр", "petya", "petya@yandex.ru", LocalDate.parse("2000-01-01"));
        User commonFriend = addUser("Анна", "anna", "anna@yandex.ru", LocalDate.parse("1999-03-03"));
        User onlyFirstFriend = addUser("Сергей", "sergey", "sergey@yandex.ru", LocalDate.parse("1998-04-04"));

        userRepository.addFriend(firstUser.getId(), commonFriend.getId());
        userRepository.addFriend(secondUser.getId(), commonFriend.getId());
        userRepository.addFriend(firstUser.getId(), onlyFirstFriend.getId());

        List<User> commonFriends = userRepository.findAllCommonFriends(firstUser.getId(), secondUser.getId());

        assertThat(commonFriends)
                .hasSize(1)
                .first()
                .extracting(User::getId)
                .isEqualTo(commonFriend.getId());
    }

    @Test
    public void testFindAllFriendsWhenUserNotFound() {
        assertThatThrownBy(() -> userRepository.findAllFriends(99999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void testAddFriendWhenUserNotFound() {
        User created = addUser();

        assertThatThrownBy(() -> userRepository.addFriend(created.getId(), 99999L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void testDeleteFriendWhenUserNotFound() {
        User created = addUser();

        assertThatThrownBy(() -> userRepository.deleteFriend(created.getId(), 99999L))
                .isInstanceOf(NotFoundException.class);
    }
}
