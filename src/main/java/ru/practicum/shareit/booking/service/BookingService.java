package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingService {
    BookingResponseDto addBooking (Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto setBookingStatus(Long userId, Long id, Boolean approved);

    BookingResponseDto getBookingById(Long userId, Long id);
}
