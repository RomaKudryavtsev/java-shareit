package ru.practicum.shareit.booking.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.projection.BookingShort;

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
    Page<BookingShort> findAllByBookerIdOrderByStartDesc(Long bookerId, Pageable page);

    @Query("select new ru.practicum.shareit.booking.projection.BookingShort(b.id, b.start, b.end, b.status, " +
            "b.booker.id, b.item.id, b.item.name) " +
            "from Booking b where b.status = ?2 and b.booker.id = ?1 order by b.start desc")
    Page<BookingShort> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable page);

    @Query("select new ru.practicum.shareit.booking.projection.BookingShort(b.id, b.start, b.end, b.status, " +
            "b.booker.id, b.item.id, b.item.name) " +
            "from Booking as b where b.item.ownerId = ?1 order by b.start desc")
    Page<BookingShort> findAllByOwnerIdOrderByStartDesc(Long ownerId, Pageable page);

    @Query("select new ru.practicum.shareit.booking.projection.BookingShort(b.id, b.start, b.end, b.status, " +
            "b.booker.id, b.item.id, b.item.name) " +
            "from Booking as b where b.status = ?2 and b.item.ownerId = ?1 order by b.start desc")
    Page<BookingShort> findAllByOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable page);
}
