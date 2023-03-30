package ru.practicum.shareit.booking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.projection.BookingShort;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Booking b set b.status = ?2 where b.id = ?1")
    void updateStatus(Long id, BookingStatus status);

    @Query("select new ru.practicum.shareit.booking.projection.BookingShort(b.id, b.start, b.end, b.status, " +
            "b.booker.id, b.item.id, b.item.name) " +
            "from Booking b where b.id = ?1")
    BookingShort findBookingShortByBookingId(Long bookingId);

    @Query("select new ru.practicum.shareit.booking.projection.BookingShort(b.id, b.start, b.end, b.status, " +
            "b.booker.id, b.item.id, b.item.name) " +
            "from Booking b where b.booker.id = ?1 order by b.start desc")
    List<BookingShort> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select new ru.practicum.shareit.booking.projection.BookingShort(b.id, b.start, b.end, b.status, " +
            "b.booker.id, b.item.id, b.item.name) " +
            "from Booking b where b.status = ?2 and b.booker.id = ?1 order by b.start desc")
    List<BookingShort> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query("select new ru.practicum.shareit.booking.projection.BookingShort(b.id, b.start, b.end, b.status, " +
            "b.booker.id, b.item.id, b.item.name) " +
            "from Booking as b where b.item.ownerId = ?1 order by b.start desc")
    List<BookingShort> findAllByOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select new ru.practicum.shareit.booking.projection.BookingShort(b.id, b.start, b.end, b.status, " +
            "b.booker.id, b.item.id, b.item.name) " +
            "from Booking as b where b.status = ?2 and b.item.ownerId = ?1 order by b.start desc")
    List<BookingShort> findAllByOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);
}
