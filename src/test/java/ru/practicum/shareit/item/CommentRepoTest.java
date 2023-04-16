package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.projection.CommentWithAuthorName;
import ru.practicum.shareit.item.repo.CommentRepo;
import ru.practicum.shareit.item.repo.ItemRepo;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepo;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepo;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase
public class CommentRepoTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private CommentRepo commentRepo;
    @Autowired
    private ItemRepo itemRepo;
    @Autowired
    private RequestRepo requestRepo;
    private Item item;
    private Comment comment;

    @BeforeEach
    void setUp() {
        User user = setUser("John Sharer", "js@gmail.com");
        userRepo.save(user);
        ItemRequest request = setRequest(user);
        requestRepo.save(request);
        item = setItem(user, request);
        itemRepo.save(item);
        comment = setComment(user, item);
        commentRepo.save(comment);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void testFindWithAuthorName() {
        CommentWithAuthorName commentWithAuthorName = commentRepo.findWithAuthorName(comment.getId());
        Assertions.assertEquals(comment.getId(), commentWithAuthorName.getId());
        Assertions.assertEquals("Comment", commentWithAuthorName.getText());
        Assertions.assertEquals("John Sharer", commentWithAuthorName.getAuthorName());
    }

    @Test
    void testFindAllWithAuthorNameByItemId() {
        List<CommentWithAuthorName> comments = commentRepo.findAllWithAuthorNameByItemId(item.getId());
        Assertions.assertEquals(1, comments.size());
        Assertions.assertEquals(1L, comments.get(0).getId());
        Assertions.assertEquals("Comment", comments.get(0).getText());
        Assertions.assertEquals("John Sharer", comments.get(0).getAuthorName());
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
        return item;
    }

    private Comment setComment(User user, Item item) {
        Comment commentPrep = new Comment();
        commentPrep.setText("Comment");
        commentPrep.setItem(item);
        commentPrep.setAuthor(user);
        commentPrep.setCreated(LocalDateTime.now());
        return commentPrep;
    }
}
