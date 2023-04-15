package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepo;

@DataJpaTest
@AutoConfigureTestDatabase
public class UserRepoTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepo userRepo;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Sharer");
        user.setEmail("js@gmail.com");
        userRepo.save(user);
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void testPersistingUser() {
        Assertions.assertNotNull(user.getId());
    }

    @Test
    void testUpdateUserEmail() {
        userRepo.updateUserEmail(user.getId(), "johns@gmail.com");
        Assertions.assertEquals("johns@gmail.com", userRepo.findById(user.getId()).orElseThrow().getEmail());
    }

    @Test
    void testUpdateUserName() {
        userRepo.updateUserName(user.getId(), "John D. Sharer");
        Assertions.assertEquals("John D. Sharer", userRepo.findById(user.getId()).orElseThrow().getName());
    }

}
