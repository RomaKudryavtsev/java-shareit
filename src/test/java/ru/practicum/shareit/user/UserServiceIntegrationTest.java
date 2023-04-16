package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceIntegrationTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private UserService userService;
    private User addedUser;

    @BeforeEach
    void setUp() {
        addedUser = userService.addUser(setUser("user", "user@user.com"));
    }

    @Test
    void testAddUser() {
        assertThat(userService.getAllUsers(), hasSize(1));
    }

    @Test
    void testAddUserDuplicativeEmail() {
        User user = setUser("user", "user@user.com");
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> userService.addUser(user));
    }

    @Test
    void testUpdateUser() {
        User updatedUser = userService.updateUser(addedUser.getId(), setUser("updated", "update@user.com"));
        assertThat(updatedUser.getName(), equalTo("updated"));
        assertThat(updatedUser.getEmail(), equalTo("update@user.com"));
    }

    @Test
    void testUpdateUserWithSameEmail() {
        User updatedUser = userService.updateUser(addedUser.getId(), setUser("user", "user@user.com"));
        assertThat(updatedUser.getName(), equalTo("user"));
        assertThat(updatedUser.getEmail(), equalTo("user@user.com"));
    }

    @Test
    void testGetUserById() {
        assertThat(addedUser.getName(), equalTo(userService.getUserById(addedUser.getId()).getName()));
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void testGetAllUsers() {
        assertThat(userService.getAllUsers(), hasSize(1));
    }

    @Test
    void deleteUserById() {
        userService.deleteUserById(addedUser.getId());
        assertThat(userService.getAllUsers(), hasSize(0));
    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }
}
