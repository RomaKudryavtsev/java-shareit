package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepo;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepo;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepo;

@DataJpaTest
@AutoConfigureTestDatabase
public class ItemRepoTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepo itemRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RequestRepo requestRepo;
    private Item item;

    @BeforeEach
    void setUp() {
        User owner = setUser("John Sharer", "js@gmail.com");
        userRepo.save(owner);
        ItemRequest request = setRequest(owner);
        requestRepo.save(request);
        item = setItem(owner, request);
        itemRepo.save(item);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void testPersistingItem() {
        Assertions.assertNotNull(item.getId());
    }

    @Test
    void testUpdateAvailable() {
        item.setAvailable(false);
        Assertions.assertFalse(itemRepo.findById(item.getId()).orElseThrow().getAvailable());
    }

    @Test
    void testUpdateDescription() {
        item.setDescription("Updated description");
        Assertions.assertEquals("Updated description", itemRepo.findById(item.getId())
                .orElseThrow().getDescription());
    }

    @Test
    void testUpdateName() {
        item.setName("Updated name");
        Assertions.assertEquals("Updated name", itemRepo.findById(item.getId()).orElseThrow().getName());
    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemRequest setRequest(User user) {
        ItemRequest request = new ItemRequest();
        request.setDescription("Request for test item");
        request.setUser(user);
        return request;
    }

    private Item setItem(User user, ItemRequest request) {
        Item item = new Item();
        item.setName("Test item");
        item.setDescription("New available test item");
        item.setOwnerId(user.getId());
        item.setAvailable(true);
        item.setRequest(request);
        item.setComments(null);
        return item;
    }
}
