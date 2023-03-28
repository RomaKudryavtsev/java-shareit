package ru.practicum.shareit.booking.repo;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.projection.BookingWithItemAndBooker;
import ru.practicum.shareit.item.repo.ItemRepo;
import ru.practicum.shareit.user.repo.UserRepo;

public class BookingRepoImpl implements BookingCustomRepo {
    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final ItemRepo itemRepo;

    public BookingRepoImpl(@Lazy BookingRepo bookingRepo, UserRepo userRepo, ItemRepo itemRepo) {
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
    }

    @Override
    public BookingWithItemAndBooker findBookingById(Long id) {
        Booking booking = bookingRepo.findById(id).get();
        return BookingWithItemAndBooker.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(itemRepo.findById(booking.getItemId()).get())
                .booker(userRepo.findBookerById(booking.getBookerId()))
                .build();
    }
}
