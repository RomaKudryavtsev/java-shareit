package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestServiceIntegrationTest {
    @Autowired
    EntityManager em;
    @Autowired
    RequestService requestService;
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;
    User addedRequester;
    User addedOwner;
    ItemRequestDto addedRequest;

    @BeforeEach
    void setUp() {
        addedRequester = userService.addUser(setUser("Requester", "req@user.com"));
        addedRequest = requestService.addItemRequest(addedRequester.getId(), setRequest("Request"));
        addedOwner = userService.addUser(setUser("Owner", "owner@user.com"));
        itemService.addItem(addedOwner.getId(), setItem(addedRequest));
    }

    @Test
    void testAddRequest() {
        assertThat(requestService.getUsersRequestsWithItems(addedRequester.getId()), hasSize(1));
    }

    @Test
    void testGetUsersRequestsWithItems() {
        Long requesterId = addedRequester.getId();
        assertThat(requestService.getUsersRequestsWithItems(requesterId), hasSize(1));
        assertThat(requestService.getUsersRequestsWithItems(requesterId).get(0).getItems(), hasSize(1));
        assertThat(requestService.getUsersRequestsWithItems(requesterId).get(0).getDescription(),
                equalTo("Request"));
        Assertions.assertThrows(UserNotFoundException.class, () -> requestService.getUsersRequestsWithItems(99L));

    }

    @Test
    void testGetRequestByIdWithItems() {
        Long requestId = addedRequest.getId();
        Long requesterId = addedRequester.getId();
        assertThat(requestService.getRequestByIdWithItems(requesterId, requestId).getItems(), hasSize(1));
        assertThat(requestService.getRequestByIdWithItems(requesterId, requestId).getItems().get(0).getName(),
                equalTo("Item"));
        Assertions.assertThrows(RequestNotFoundException.class,
                () -> requestService.getRequestByIdWithItems(requesterId, 99L));
    }

    @Test
    void testGetAllRequestsOfOtherUsers() {
        Long ownerId = addedOwner.getId();
        assertThat(requestService.getAllRequestsOfOtherUsers(ownerId, 0, 10), hasSize(1));
        Assertions.assertThrows(UserNotFoundException.class,
                () -> requestService.getAllRequestsOfOtherUsers(99L, 0, 10));
    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemRequestDto setRequest(String description) {
        return ItemRequestDto.builder()
                .description(description)
                .build();
    }

    private ItemDto setItem(ItemRequestDto request) {
        return ItemDto.builder()
                .name("Item")
                .description("Description")
                .available(true)
                .requestId(request.getId())
                .build();
    }
}
