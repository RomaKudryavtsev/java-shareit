package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingAddRequestDto;
import ru.practicum.shareit.booking.projection.BookingWithItemAndBooker;

public interface BookingService {
    BookingWithItemAndBooker addBooking (Long userId, BookingAddRequestDto bookingDto);
}
