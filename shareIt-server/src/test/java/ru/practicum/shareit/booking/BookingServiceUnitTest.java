package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.projection.BookingShort;
import ru.practicum.shareit.booking.repo.BookingRepo;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.WrongStatusException;
import ru.practicum.shareit.item.projection.ItemShort;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.projection.UserShort;
import ru.practicum.shareit.user.repo.UserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceUnitTest {
    @Mock
    BookingRepo bookingRepoMock;
    @Mock
    UserRepo userRepoMock;
    @InjectMocks
    BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        User user = setUser(1L, "John Sharer", "js@gmail.com");
        Mockito.when(userRepoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(user));

        LocalDateTime timeInPast = LocalDateTime.now().minusYears(1);
        LocalDateTime timeInFuture = LocalDateTime.now().plusYears(1);
        LocalDateTime timeCurrent = LocalDateTime.now();

        BookingShort bookingInPast = setBooking(1L, timeInPast);
        BookingShort bookingInFuture = setBooking(2L, timeInFuture);
        BookingShort bookingCurrent = setBooking(3L, timeCurrent);

        Page<BookingShort> page = new PageImpl<>(List.of(bookingInPast, bookingInFuture, bookingCurrent));

        Mockito.lenient().when(bookingRepoMock.findAllByBookerIdOrderByStartDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);
        Mockito.lenient().when(bookingRepoMock.findAllByBookerIdAndStatusOrderByStartDesc(Mockito.anyLong(),
                Mockito.any(), Mockito.any())).thenReturn(page);
        Mockito.lenient().when(bookingRepoMock.findAllByOwnerIdOrderByStartDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);
        Mockito.lenient().when(bookingRepoMock.findAllByOwnerIdAndStatusOrderByStartDesc(Mockito.anyLong(),
                Mockito.any(), Mockito.any())).thenReturn(page);
    }

    @Test
    void testGetAllBookingsOfBookerPast() {
        List<BookingResponseDto> bookingsInPast = bookingService
                .getAllBookingsOfBookerByState(1L, "PAST", 0, 10);
        assertThat(bookingsInPast, hasSize(1));
        assertThat(bookingsInPast.get(0).getId(), equalTo(1L));
    }

    @Test
    void testGetAllBookingsOfBookerFuture() {
        List<BookingResponseDto> bookingsInFuture = bookingService
                .getAllBookingsOfBookerByState(1L, "FUTURE", 0, 10);
        assertThat(bookingsInFuture, hasSize(1));
        assertThat(bookingsInFuture.get(0).getId(), equalTo(2L));
    }

    @Test
    void testGetAllBookingsOfBookerCurrent() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
        List<BookingResponseDto> bookingsCurrent = bookingService
                .getAllBookingsOfBookerByState(1L, "CURRENT", 0, 10);
        assertThat(bookingsCurrent, hasSize(1));
        assertThat(bookingsCurrent.get(0).getId(), equalTo(3L));
    }

    @Test
    void testGetAllBookingsOfBooker() {
        List<BookingResponseDto> bookingsAll = bookingService
                .getAllBookingsOfBookerByState(1L, "ALL", 0, 10);
        assertThat(bookingsAll, hasSize(3));
    }

    @Test
    void testGetAllBookingsOfBookerApproved() {
        List<BookingResponseDto> bookingsApproved = bookingService
                .getAllBookingsOfBookerByState(1L, "APPROVED", 0, 10);
        assertThat(bookingsApproved, hasSize(3));
    }

    @Test
    void testGetAllBookingsOfOwnerPast() {
        List<BookingResponseDto> bookingsInPast = bookingService
                .getAllBookingsOfOwnerByState(1L, "PAST", 0, 10);
        assertThat(bookingsInPast, hasSize(1));
        assertThat(bookingsInPast.get(0).getId(), equalTo(1L));
    }

    @Test
    void testGetAllBookingsOfOwnerFuture() {
        List<BookingResponseDto> bookingsInFuture = bookingService
                .getAllBookingsOfOwnerByState(1L, "FUTURE", 0, 10);
        assertThat(bookingsInFuture, hasSize(1));
        assertThat(bookingsInFuture.get(0).getId(), equalTo(2L));
    }

    @Test
    void testGetAllBookingsOfOwnerCurrent() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
        List<BookingResponseDto> bookingsCurrent = bookingService
                .getAllBookingsOfOwnerByState(1L, "CURRENT", 0, 10);
        assertThat(bookingsCurrent, hasSize(1));
        assertThat(bookingsCurrent.get(0).getId(), equalTo(3L));
    }

    @Test
    void testGetAllBookingsOfOwner() {
        List<BookingResponseDto> bookingsAll = bookingService
                .getAllBookingsOfOwnerByState(1L, "ALL", 0, 10);
        assertThat(bookingsAll, hasSize(3));
    }

    @Test
    void testGetAllBookingsOfOwnerApproved() {
        List<BookingResponseDto> bookingsApproved = bookingService
                .getAllBookingsOfOwnerByState(1L, "APPROVED", 0, 10);
        assertThat(bookingsApproved, hasSize(3));
    }

    @Test
    void testGetAllBookingsWrongStatusFail() {
        Assertions.assertThrows(WrongStatusException.class,
                () -> bookingService.getAllBookingsOfBookerByState(1L, "UNKNOWN", 0, 10));
    }

    private User setUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private BookingShort setBooking(Long id, LocalDateTime start) {
        return new BookingShort(id, start, start.plusDays(1),
                BookingStatus.APPROVED, new UserShort(1L), new ItemShort(1L, "Item"));
    }
}
