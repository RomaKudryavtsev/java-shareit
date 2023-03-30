package ru.practicum.shareit.booking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.projection.BookingFull;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Booking b set b.status = ?2 where b.id = ?1")
    void updateStatus(Long id, BookingStatus status);

    @Query("select new ru.practicum.shareit.booking.projection.BookingFull(b.id, b.start, b.end, b.status, b.booker, b.item) " +
            "from Booking as b join b.booker join b.item where b.booker.id = ?1 order by b.start desc")
    List<BookingFull> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select new ru.practicum.shareit.booking.projection.BookingFull(b.id, b.start, b.end, b.status, b.booker, b.item) " +
            "from Booking as b join b.booker join b.item where b.status = ?2 and b.booker.id = ?1 order by b.start desc")
    List<BookingFull> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query("select new ru.practicum.shareit.booking.projection.BookingFull(b.id, b.start, b.end, b.status, b.booker, b.item) " +
            "from Booking as b join b.booker join b.item where b.item.ownerId = ?1 order by b.start desc")
    List<BookingFull> findAllByOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select new ru.practicum.shareit.booking.projection.BookingFull(b.id, b.start, b.end, b.status, b.booker, b.item) " +
            "from Booking as b join b.booker join b.item where b.status = ?2 and b.item.ownerId = ?1 order by b.start desc")
    List<BookingFull> findAllByOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);
}
