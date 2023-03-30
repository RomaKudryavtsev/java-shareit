package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.projection.BookingShort;
import ru.practicum.shareit.booking.repo.BookingRepo;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.repo.ItemRepo;
import ru.practicum.shareit.user.repo.UserRepo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final ItemRepo itemRepo;
    private final Supplier<BookingNotFoundException> bookingNotFoundSupplier =
            () -> {throw new BookingNotFoundException("Booking does not exist");};
    private final Supplier<UserNotFoundException> userNotFoundSupplier =
            () -> {throw new UserNotFoundException("User does not exist");};
    private final Supplier<ItemNotFoundException> itemNotFoundSupplier =
            () -> {throw new ItemNotFoundException("Item does not exist");};
    private final Function<Instant, Predicate<BookingShort>> currentBookingsFunction = now ->
            b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now);
    private final Function<Instant, Predicate<BookingShort>> pastBookingsFunction = now ->
            b -> b.getEnd().isBefore(now);
    private final Function<Instant, Predicate<BookingShort>> futureBookingsFunction = now ->
            b -> b.getStart().isAfter(now);


    @Autowired
    public BookingServiceImpl(BookingRepo bookingRepo, UserRepo userRepo, ItemRepo itemRepo) {
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
    }

    private void checkIfItemIsAvailable(Long itemId) {
        if (!itemRepo.findById(itemId).orElseThrow(bookingNotFoundSupplier).getAvailable()) {
            throw new ItemUnavailableException("Item is not available");
        }
    }

    private void checkStartAndEnd(LocalDateTime start, LocalDateTime end) {
        if (start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now())) {
            throw new WrongDatesException("Start or end in the past");
        }
        if (start.isEqual(end) || start.isAfter(end)) {
            throw new WrongDatesException("Start has to precede end");
        }
    }

    private void checkIfOwnerIsApproving(Long userId, Long id) {
        if (!bookingRepo.findById(id).orElseThrow(bookingNotFoundSupplier).getItem().getOwnerId().equals(userId)) {
            throw new NonOwnerUpdatingException("Booking may be approved only by the owner");
        }
    }

    private void checkIfStatusUpdateIsBeforeApproval(long id) {
        if (!bookingRepo.findById(id).orElseThrow(bookingNotFoundSupplier).getStatus().equals(BookingStatus.WAITING)) {
            throw new UpdateStatusAfterApprovalException("Booking status may not be changed after approval");
        }
    }

    private void checkIfBookerOrOwnerIsRequesting(Long userId, Long id) {
        Long ownerId = bookingRepo.findById(id)
                .orElseThrow(bookingNotFoundSupplier)
                .getItem().getOwnerId();
        Long bookerId = bookingRepo.findById(id)
                .orElseThrow(bookingNotFoundSupplier)
                .getBooker().getId();
        if (!userId.equals(ownerId) && !userId.equals(bookerId)) {
            throw new NonOwnerUpdatingException("Booking may be viewed by its booker or owner");
        }
    }

    private void checkIfOwnerExists(Long ownerId) {
        if (userRepo.findById(ownerId).isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }
    }

    private void checkIfBookingExists(Long bookingId) {
        if(bookingRepo.findById(bookingId).isEmpty()) {
            throw new BookingNotFoundException("Booking does not exist");
        }
    }

    private void checkIfBookerIsNotOwner(Long userId, Long itemId) {
        if (userId.equals(itemRepo.findById(itemId).orElseThrow(itemNotFoundSupplier).getOwnerId())) {
            throw new BookerIsOwnerException("Owner may not book his own item");
        }
    }

    private BookingStatus parseStatus(String state) {
        try {
            return BookingStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new WrongDatesException(String.format("Unknown state: %s", state));
        }
    }

    @Transactional
    @Override
    public BookingResponseDto addBooking(Long userId, BookingRequestDto bookingRequestDto) {
        checkIfItemIsAvailable(bookingRequestDto.getItemId());
        checkStartAndEnd(bookingRequestDto.getStart(), bookingRequestDto.getEnd());
        checkIfBookerIsNotOwner(userId, bookingRequestDto.getItemId());
        Booking newBooking = BookingMapper.mapDtoToModel(bookingRequestDto);
        newBooking.setItem(itemRepo.findById(bookingRequestDto.getItemId())
                .orElseThrow(itemNotFoundSupplier));
        newBooking.setBooker(userRepo.findById(userId)
                .orElseThrow(userNotFoundSupplier));
        newBooking.setStatus(BookingStatus.WAITING);
        Booking addedBooking = bookingRepo.save(newBooking);
        return BookingMapper.mapModelToDto(addedBooking);
    }

    @Transactional
    @Override
    public BookingResponseDto setBookingStatus(Long userId, Long id, Boolean approved) {
        checkIfOwnerIsApproving(userId, id);
        checkIfStatusUpdateIsBeforeApproval(id);
        if (approved) {
            bookingRepo.updateStatus(id, BookingStatus.APPROVED);
        } else {
            bookingRepo.updateStatus(id, BookingStatus.REJECTED);
        }
        Booking bookingUpdated = bookingRepo.findById(id).orElseThrow();
        return BookingMapper.mapModelToDto(bookingUpdated);
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long id) {
        checkIfBookerOrOwnerIsRequesting(userId, id);
        checkIfBookingExists(id);
        return BookingMapper.mapProjectionToDto(bookingRepo.findBookingShortByBookingId(id));
    }

    @Override
    public List<BookingResponseDto> getAllBookingsOfBookerByState(Long bookerId, String state) {
        checkIfOwnerExists(bookerId);
        BookingStatus requestedStatus = parseStatus(state);
        Instant now = Instant.now();
        switch (requestedStatus) {
            case ALL:
                return bookingRepo.findAllByBookerIdOrderByStartDesc(bookerId).stream()
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepo.findAllByBookerIdOrderByStartDesc(bookerId).stream()
                        .filter(currentBookingsFunction.apply(now))
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            case PAST:
                return bookingRepo.findAllByBookerIdOrderByStartDesc(bookerId).stream()
                        .filter(pastBookingsFunction.apply(now))
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepo.findAllByBookerIdOrderByStartDesc(bookerId).stream()
                        .filter(futureBookingsFunction.apply(now))
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            default:
                return bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, requestedStatus).stream()
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingResponseDto> getAllBookingsOfOwnerByState(Long ownerId, String state) {
        checkIfOwnerExists(ownerId);
        BookingStatus requestedStatus = parseStatus(state);
        Instant now = Instant.now();
        switch (requestedStatus) {
            case ALL:
                return bookingRepo.findAllByOwnerIdOrderByStartDesc(ownerId).stream()
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepo.findAllByOwnerIdOrderByStartDesc(ownerId).stream()
                        .filter(currentBookingsFunction.apply(now))
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            case PAST:
                return bookingRepo.findAllByOwnerIdOrderByStartDesc(ownerId).stream()
                        .filter(pastBookingsFunction.apply(now))
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepo.findAllByOwnerIdOrderByStartDesc(ownerId).stream()
                        .filter(futureBookingsFunction.apply(now))
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            default:
                return bookingRepo.findAllByOwnerIdAndStatusOrderByStartDesc(ownerId, requestedStatus).stream()
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
        }
    }
}
