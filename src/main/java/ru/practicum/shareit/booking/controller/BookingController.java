package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingAddRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.projection.BookingWithItemAndBooker;
import ru.practicum.shareit.booking.service.BookingService;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    public BookingController (BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingWithItemAndBooker addBooking(@RequestHeader(USER_HEADER) Long userId, @RequestBody BookingAddRequestDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }
}
