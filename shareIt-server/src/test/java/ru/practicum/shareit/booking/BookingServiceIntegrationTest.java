package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceIntegrationTest {
    @Autowired
    EntityManager em;
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    @Autowired
    RequestService requestService;
    @Autowired
    BookingService bookingService;
    User addedOwner;
    User addedBooker;
    ItemRequestDto addedRequest;
    ItemDto addedItem;
    BookingResponseDto addedBooking;

    @BeforeEach
    void setUp() {
        User owner = setUser("John Sharer", "js@gmail.com");
        addedOwner = userService.addUser(owner);
        User booker = setUser("James Booker", "jb@gmail.com");
        addedBooker = userService.addUser(booker);
        ItemRequestDto requestDto = setRequestDto("Request");
        addedRequest = requestService.addItemRequest(addedBooker.getId(), requestDto);
        ItemDto itemDto = setItemDto(addedRequest);
        addedItem = itemService.addItem(addedOwner.getId(), itemDto);
        BookingRequestDto bookingDto = setFutureBookingDto(addedItem);
        addedBooking = bookingService.addBooking(addedBooker.getId(), bookingDto);
    }

    @Test
    void testRejectedStatus() {
        bookingService.setBookingStatus(addedOwner.getId(), addedBooking.getId(), false);
        assertThat(bookingService.getBookingById(addedOwner.getId(), addedItem.getId()).getStatus(),
                equalTo(BookingStatus.REJECTED));
        Assertions.assertThrows(UpdateStatusAfterApprovalException.class,
                () -> bookingService.setBookingStatus(addedOwner.getId(), addedBooking.getId(), true));
    }

    @Test
    void testAddBookingByOwnerFail() {
        Assertions.assertThrows(BookerIsOwnerException.class,
                () -> bookingService.addBooking(addedOwner.getId(), setFutureBookingDto(addedItem)));
    }

    @Test
    void testAddBookingWithWrongDate() {
        BookingRequestDto bookingWrongDate = setFutureBookingDto(addedItem);
        bookingWrongDate.setStart(LocalDateTime.now());
        bookingWrongDate.setEnd(LocalDateTime.now());
        Assertions.assertThrows(WrongDatesException.class,
                () -> bookingService.addBooking(addedBooker.getId(), bookingWrongDate));
    }

    @Test
    void testAddBookingUnavailableItemFail() {
        ItemDto itemUpdated = addedItem;
        itemUpdated.setAvailable(false);
        itemService.updateItem(addedOwner.getId(), itemUpdated.getId(), itemUpdated);
        Assertions.assertThrows(ItemUnavailableException.class,
                () -> bookingService.addBooking(addedBooker.getId(), setFutureBookingDto(itemUpdated)));
    }

    @Test
    void testGetBookingById() {
        assertThat(bookingService.getBookingById(addedOwner.getId(), addedBooking.getId()).getStatus(),
                equalTo(BookingStatus.WAITING));
        Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(addedOwner.getId(), 99L));
    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemRequestDto setRequestDto(String description) {
        return ItemRequestDto.builder()
                .description(description)
                .build();
    }

    private ItemDto setItemDto(ItemRequestDto requestDto) {
        return ItemDto.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .requestId(requestDto.getId())
                .build();
    }

    private BookingRequestDto setFutureBookingDto(ItemDto itemDto) {
        BookingRequestDto bookingDto = new BookingRequestDto();
        LocalDateTime nextStart = LocalDateTime.now().plusDays(1);
        LocalDateTime nextEnd = nextStart.plusDays(2);
        bookingDto.setStart(nextStart);
        bookingDto.setEnd(nextEnd);
        bookingDto.setItemId(itemDto.getId());
        return bookingDto;
    }
}
