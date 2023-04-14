package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto addBooking(Long userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto setBookingStatus(Long userId, Long id, Boolean approved);

    BookingResponseDto getBookingById(Long userId, Long id);

    List<BookingResponseDto> getAllBookingsOfBookerByState(Long bookerId, String state, int from, int size);

    List<BookingResponseDto> getAllBookingsOfOwnerByState(Long ownerId, String state, int from, int size);
}
