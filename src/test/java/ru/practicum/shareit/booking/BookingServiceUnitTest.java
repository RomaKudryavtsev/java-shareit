package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.projection.BookingShort;
import ru.practicum.shareit.booking.repo.BookingRepo;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.projection.ItemShort;
import ru.practicum.shareit.item.repo.ItemRepo;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.projection.UserShort;
import ru.practicum.shareit.user.repo.UserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {
    @Mock
    BookingRepo bookingRepoMock;
    @Mock
    UserRepo userRepoMock;
    @InjectMocks
    BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        LocalDateTime testTime = LocalDateTime.of(2022, 4, 16, 12, 0, 0);
        BookingShort booking = new BookingShort(1L, testTime, testTime.plusDays(1),
                BookingStatus.APPROVED, new UserShort(1L), new ItemShort(1L, "Item"));
        Page<BookingShort> page = new PageImpl<>(List.of(booking));
        Mockito.lenient().when(bookingRepoMock.findAllByBookerIdOrderByStartDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);
        Mockito.lenient().when(bookingRepoMock.findAllByOwnerIdOrderByStartDesc(Mockito.anyLong(), Mockito.any()))
                .thenReturn(page);
        User user = new User();
        user.setId(1L);
        user.setName("John Sharer");
        user.setEmail("js@gmail.com");
        Mockito.when(userRepoMock.findById(Mockito.anyLong())).thenReturn(Optional.of(user));
    }

    @Test
    void testGetAllBookingsOfBookerByState() {
        List<BookingResponseDto> bookings = bookingService
                .getAllBookingsOfBookerByState(1L, "PAST", 0, 10);
        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }

    @Test
    void testGetAllBookingsOfOwnerByState() {
        List<BookingResponseDto> bookings = bookingService
                .getAllBookingsOfOwnerByState(1L, "PAST", 0, 10);
        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), equalTo(1L));
    }
}
