package ru.practicum.shareit.booking.repo;

import ru.practicum.shareit.booking.projection.BookingWithItemAndBooker;

public interface BookingCustomRepo {
    BookingWithItemAndBooker findBookingById(Long id);
}
