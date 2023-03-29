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
import ru.practicum.shareit.booking.repo.BookingRepo;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepo;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repo.UserRepo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepo bookingRepo;
    private final UserRepo userRepo;
    private final ItemRepo itemRepo;
    private final Supplier<BookingNotFoundException> bookingNotFound =
            () -> {
                throw new BookingNotFoundException("Booking does not exist");
            };
    private final Supplier<UserNotFoundException> userNotFound =
            () -> {
                throw new UserNotFoundException("User does not exist");
            };
    private final Supplier<ItemNotFoundException> itemNotFound =
            () -> {
                throw new ItemNotFoundException("Item does not exist");
            };

    @Autowired
    public BookingServiceImpl(BookingRepo bookingRepo, UserRepo userRepo, ItemRepo itemRepo) {
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
    }

    private void checkIfItemIsAvailable(Long itemId) {
        if (!itemRepo.findById(itemId).orElseThrow(bookingNotFound).getAvailable()) {
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
        if (!bookingRepo.findById(id).orElseThrow(bookingNotFound).getItem().getOwnerId().equals(userId)) {
            throw new NonOwnerUpdatingException("Booking may be approved only by the owner");
        }
    }

    private void checkIfStatusUpdateIsBeforeApproval(long id) {
        if(!bookingRepo.findById(id).orElseThrow(bookingNotFound).getStatus().equals(BookingStatus.WAITING)) {
            throw new UpdateStatusAfterApprovalException("Booking status may not be changed after approval");
        }
    }

    private void checkIfBookerOrOwnerIsRequesting(Long userId, Long id) {
        Long ownerId = bookingRepo.findById(id)
                .orElseThrow(bookingNotFound)
                .getItem().getOwnerId();
        Long bookerId = bookingRepo.findById(id)
                .orElseThrow(bookingNotFound)
                .getBooker().getId();
        if (!userId.equals(ownerId) && !userId.equals(bookerId)) {
            throw new NonOwnerUpdatingException("Booking may be viewed by its booker or owner");
        }
    }

    private void checkIfOwnerExists(Long ownerId) {
        if(userRepo.findById(ownerId).isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }
    }

    private void checkIfBookerIsNotOwner(Long userId, Long itemId) {
        if(userId.equals(itemRepo.findById(itemId).orElseThrow(itemNotFound).getOwnerId())) {
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
                .orElseThrow(itemNotFound));
        newBooking.setBooker(userRepo.findById(userId)
                .orElseThrow(userNotFound));
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
        return BookingMapper.mapModelToDto(bookingRepo.findById(id).orElseThrow(bookingNotFound));
    }

    @Override
    public List<BookingResponseDto> getAllBookingsOfBookerByState(Long bookerId, String state) {
        BookingStatus requestedStatus = parseStatus(state);
        Instant now = Instant.now();
        User booker = userRepo.findById(bookerId).orElseThrow(userNotFound);
        switch(requestedStatus) {
            case ALL:
                return bookingRepo.findAllByBookerOrderByStartDesc(booker).stream()
                        .map(BookingMapper::mapModelToDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepo.findAllByBookerOrderByStartDesc(booker).stream()
                        .filter(b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now))
                        .map(BookingMapper::mapModelToDto).collect(Collectors.toList());
            case PAST:
                return bookingRepo.findAllByBookerOrderByStartDesc(booker).stream()
                        .filter(b -> b.getEnd().isBefore(now))
                        .map(BookingMapper::mapModelToDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepo.findAllByBookerOrderByStartDesc(booker).stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .map(BookingMapper::mapModelToDto).collect(Collectors.toList());
            default:
                return bookingRepo.findAllByBookerAndStatusOrderByStartDesc(booker, requestedStatus).stream()
                        .map(BookingMapper::mapModelToDto).collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingResponseDto> getAllBookingsOfOwnerByState(Long ownerId, String state) {
        checkIfOwnerExists(ownerId);
        BookingStatus requestedStatus = parseStatus(state);
        Instant now = Instant.now();
        List<Item> ownersItems = itemRepo.findAllByOwnerId(ownerId);
        switch(requestedStatus) {
            case ALL:
                return ownersItems.stream().map(bookingRepo::findAllByItem)
                        .flatMap(Collection::stream)
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(BookingMapper::mapModelToDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return ownersItems.stream().map(bookingRepo::findAllByItem)
                        .flatMap(Collection::stream)
                        .filter(b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(BookingMapper::mapModelToDto)
                        .collect(Collectors.toList());
            case PAST:
                return ownersItems.stream().map(bookingRepo::findAllByItem)
                        .flatMap(Collection::stream)
                        .filter(b -> b.getEnd().isBefore(now))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(BookingMapper::mapModelToDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return ownersItems.stream().map(bookingRepo::findAllByItem)
                        .flatMap(Collection::stream)
                        .filter(b -> b.getStart().isAfter(now))
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(BookingMapper::mapModelToDto)
                        .collect(Collectors.toList());
            default:
                return ownersItems.stream().map(item -> bookingRepo.findAllByItemAndStatus(item, requestedStatus))
                        .flatMap(Collection::stream)
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .map(BookingMapper::mapModelToDto)
                        .collect(Collectors.toList());
        }
    }
}
