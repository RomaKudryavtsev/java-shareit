package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.projection.BookingShort;
import ru.practicum.shareit.booking.repo.BookingRepo;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepo;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepo;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepo;

import java.time.LocalDateTime;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@DataJpaTest
@AutoConfigureTestDatabase
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRepoTest {
    @Autowired
    BookingRepo bookingRepo;
    @Autowired
    TestEntityManager em;
    @Autowired
    ItemRepo itemRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    RequestRepo requestRepo;
    Booking booking;
    User booker;
    User owner;
    Item item;
    final Pageable request = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        owner = setUser("John Sharer", "js@gmail.com");
        booker = setUser("James Booker", "jb@gmail.com");
        userRepo.save(owner);
        userRepo.save(booker);
        ItemRequest request = setRequest("Request for test item", owner);
        requestRepo.save(request);
        item = setItem(owner, request);
        itemRepo.save(item);
        booking = setBooking(booker, item);
        bookingRepo.save(booking);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void testUpdateStatus() {
        bookingRepo.updateStatus(booking.getId(), APPROVED);
        Assertions.assertEquals(APPROVED, bookingRepo.findById(booking.getId()).orElseThrow().getStatus());
    }

    @Test
    void testFindBookingShortByBookingId() {
        BookingShort bookingShort = bookingRepo.findBookingShortByBookingId(booking.getId());
        Assertions.assertEquals(booking.getId(), bookingShort.getId());
        Assertions.assertEquals(booking.getStatus(), bookingShort.getStatus());
        Assertions.assertEquals(booker.getId(), bookingShort.getBooker().getId());
        Assertions.assertEquals(item.getId(), bookingShort.getItem().getId());
    }

    @Test
    void testFindAllByBookerIdOrderByStartDesc() {
        Page<BookingShort> bookings = bookingRepo.findAllByBookerIdOrderByStartDesc(booker.getId(), request);
        Assertions.assertEquals(1, bookings.getContent().size());
        Assertions.assertEquals(booking.getId(), bookings.getContent().get(0).getId());
        Assertions.assertEquals(booking.getStatus(), bookings.getContent().get(0).getStatus());
        Assertions.assertEquals(booker.getId(), bookings.getContent().get(0).getBooker().getId());
        Assertions.assertEquals(item.getId(), bookings.getContent().get(0).getItem().getId());
    }

    @Test
    void testFindAllByBookerIdAndStatusOrderByStartDesc() {
        Page<BookingShort> bookings = bookingRepo
                .findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), WAITING, request);
        Assertions.assertEquals(1, bookings.getContent().size());
        Assertions.assertEquals(booking.getId(), bookings.getContent().get(0).getId());
        Assertions.assertEquals(booking.getStatus(), bookings.getContent().get(0).getStatus());
        Assertions.assertEquals(booker.getId(), bookings.getContent().get(0).getBooker().getId());
        Assertions.assertEquals(item.getId(), bookings.getContent().get(0).getItem().getId());
    }

    @Test
    void testFindAllByOwnerIdOrderByStartDesc() {
        Page<BookingShort> bookings = bookingRepo
                .findAllByOwnerIdOrderByStartDesc(owner.getId(), request);
        Assertions.assertEquals(1, bookings.getContent().size());
        Assertions.assertEquals(booking.getId(), bookings.getContent().get(0).getId());
        Assertions.assertEquals(booking.getStatus(), bookings.getContent().get(0).getStatus());
        Assertions.assertEquals(booker.getId(), bookings.getContent().get(0).getBooker().getId());
        Assertions.assertEquals(item.getId(), bookings.getContent().get(0).getItem().getId());
    }

    @Test
    void testFindAllByOwnerIdAndStatusOrderByStartDesc() {
        Page<BookingShort> bookings = bookingRepo
                .findAllByOwnerIdAndStatusOrderByStartDesc(owner.getId(), WAITING, request);
        Assertions.assertEquals(1, bookings.getContent().size());
        Assertions.assertEquals(booking.getId(), bookings.getContent().get(0).getId());
        Assertions.assertEquals(booking.getStatus(), bookings.getContent().get(0).getStatus());
        Assertions.assertEquals(booker.getId(), bookings.getContent().get(0).getBooker().getId());
        Assertions.assertEquals(item.getId(), bookings.getContent().get(0).getItem().getId());
    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemRequest setRequest(String description, User user) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
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

    private Booking setBooking(User user, Item item) {
        Booking bookingPrep = new Booking();
        bookingPrep.setStart(LocalDateTime.now());
        bookingPrep.setEnd(LocalDateTime.now().plusDays(1));
        bookingPrep.setStatus(BookingStatus.WAITING);
        bookingPrep.setItem(item);
        bookingPrep.setBooker(user);
        return bookingPrep;
    }
}
