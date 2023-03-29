package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repo.BookingRepo;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.WrongDatesException;
import ru.practicum.shareit.item.repo.ItemRepo;
import ru.practicum.shareit.user.repo.UserRepo;

import java.time.LocalDateTime;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final ItemRepo itemRepo;

    @Autowired
    public BookingServiceImpl(BookingRepo bookingRepo, UserRepo userRepo, ItemRepo itemRepo) {
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
    }

    private void checkIfItemIsAvailable(Long itemId) {
        if(!itemRepo.findById(itemId)
                .orElseThrow(() -> {throw new ItemNotFoundException("Item does not exist");})
                .getAvailable()) {
            throw new ItemUnavailableException("Item is not available");
        }
    }

    private void checkStartAndEnd(LocalDateTime start, LocalDateTime end) {
        if(start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now())) {
            throw new WrongDatesException("Start or end in the past");
        }
        if(start.isEqual(end) || start.isAfter(end)) {
            throw new WrongDatesException("Start has to precede end");
        }
    }

    @Override
    public BookingResponseDto addBooking(Long userId, BookingRequestDto bookingRequestDto) {
        checkIfItemIsAvailable(bookingRequestDto.getItemId());
        checkStartAndEnd(bookingRequestDto.getStart(), bookingRequestDto.getEnd());
        Booking newBooking = BookingMapper.mapDtoToModel(bookingRequestDto);
        newBooking.setItem(itemRepo.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> {throw new ItemNotFoundException("Item does not exist");}));
        newBooking.setBooker(userRepo.findById(userId)
                .orElseThrow(() -> {throw new UserNotFoundException("User does not exist");}));
        newBooking.setStatus(BookingStatus.WAITING);
        Booking addedBooking = bookingRepo.save(newBooking);
        return BookingMapper.mapModelToDto(addedBooking);
    }
}
