package ru.practicum.shareit.booking.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Long>, BookingCustomRepo {
}
