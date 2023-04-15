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
        User owner = new User();
        owner.setName("John Sharer");
        owner.setEmail("js@gmail.com");
        userRepo.save(owner);
        ItemRequest request = new ItemRequest();
        request.setDescription("Request for test item");
        request.setUser(owner);
        requestRepo.save(request);
        item = new Item();
        item.setName("Test item");
        item.setDescription("New available test item");
        item.setOwnerId(owner.getId());
        item.setAvailable(true);
        item.setRequest(request);
        itemRepo.save(item);
        comment = new Comment();
        comment.setText("Comment");
        comment.setItem(item);
        comment.setAuthor(owner);
        comment.setCreated(LocalDateTime.now());
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
}
