package ru.practicum.shareit.booking;

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
public class BookingRepoTest {
    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepo itemRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RequestRepo requestRepo;
    private Booking booking;
    private User booker;
    private User owner;
    private Item item;
    private final Pageable request = PageRequest.of(0, 10);

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setName("John Sharer");
        owner.setEmail("js@gmail.com");
        booker = new User();
        booker.setName("James Booker");
        booker.setEmail("jb@gmail.com");
        userRepo.save(owner);
        userRepo.save(booker);
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
        item.setComments(null);
        itemRepo.save(item);
        booking = new Booking();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
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
}
