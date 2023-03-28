package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingAddRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.projection.BookingWithItemAndBooker;
import ru.practicum.shareit.booking.repo.BookingRepo;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.math.BigInteger;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepo bookingRepo;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public BookingServiceImpl(BookingRepo bookingRepo, UserService userService, ItemService itemService) {
        this.bookingRepo = bookingRepo;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingWithItemAndBooker addBooking(Long userId, BookingAddRequestDto bookingDto) {
        Booking booking = BookingMapper.mapAddRequestDtoToModel(bookingDto);
        booking.setBookerId(userId);
        booking.setStatus(BookingStatus.WAITING);
        Booking addedBooking = bookingRepo.save(booking);
        return bookingRepo.findBookingById(addedBooking.getId());
    }
}
