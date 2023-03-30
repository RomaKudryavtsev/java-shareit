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

    @Query("select b " +
            "from Booking as b join fetch b.item where b.booker.id = ?1 order by b.start desc")
    List<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("select b " +
            "from Booking as b left join fetch b.item where b.status = ?2 and b.booker.id = ?1 order by b.start desc")
    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    @Query("select b " +
            "from Booking as b left join fetch b.item where b.item.ownerId = ?1 order by b.start desc")
    List<Booking> findAllByOwnerIdOrderByStartDesc(Long ownerId);

    @Query("select b " +
            "from Booking as b left join fetch b.item where b.status = ?2 and b.item.ownerId = ?1 order by b.start desc")
    List<Booking> findAllByOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);
}
