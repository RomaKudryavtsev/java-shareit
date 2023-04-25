package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final String BOOKING_PATH = "/{bookingId}";

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDto addBooking(@RequestHeader(USER_HEADER) Long userId,
                                         @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.addBooking(userId, bookingRequestDto);
    }

    @PatchMapping(value = BOOKING_PATH)
    public BookingResponseDto setBookingStatus(@RequestHeader(USER_HEADER) Long userId,
                                               @PathVariable("bookingId") Long id, @RequestParam Boolean approved) {
        return bookingService.setBookingStatus(userId, id, approved);
    }

    @GetMapping(BOOKING_PATH)
    public BookingResponseDto getBookingById(@RequestHeader(USER_HEADER) Long userId,
                                             @PathVariable("bookingId") Long id) {
        return bookingService.getBookingById(userId, id);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookingsOfBookerByState(@RequestHeader(USER_HEADER) Long bookerId,
                                                                  @RequestParam(defaultValue = "ALL", required = false) String state,
                                                                  @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                                  @RequestParam(required = false, defaultValue = "10") @PositiveOrZero int size) {
        return bookingService.getAllBookingsOfBookerByState(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsOfOwnerByState(@RequestHeader(USER_HEADER) Long ownerId,
                                                                 @RequestParam(defaultValue = "ALL", required = false) String state,
                                                                 @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                                 @RequestParam(required = false, defaultValue = "10") @PositiveOrZero int size) {
        return bookingService.getAllBookingsOfOwnerByState(ownerId, state, from, size);
    }

}
