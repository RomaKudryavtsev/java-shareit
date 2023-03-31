package ru.practicum.shareit.item.repo;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.projection.BookingShortForItem;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBooking;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemRepoImpl implements ItemRepoCustom {
    private final ItemRepo itemRepo;

    public ItemRepoImpl(@Lazy ItemRepo itemRepo) {
        this.itemRepo = itemRepo;
    }

    @Override
    public ItemWithLastAndNextBooking findItemWithLastAndNextBooking(Long itemId, LocalDateTime now, boolean isOwner) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> {throw new ItemNotFoundException("Item does not exist");});
        List<Booking> bookings = item.getBookings();
        BookingShortForItem lastBooking;
        BookingShortForItem nextBooking;
        if(bookings == null || bookings.isEmpty() || !isOwner) {
            lastBooking = null;
            nextBooking = null;
        } else {
            lastBooking = getLastBooking(bookings, now);
            nextBooking = getNextBooking(bookings, now);
        }
        return ItemWithLastAndNextBooking.builder()
                .id(item.getId())
                .ownerId(item.getOwnerId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

    @Override
    public List<ItemWithLastAndNextBooking> findAllWithLastAndNextBooking(Long ownerId, LocalDateTime now) {
        List<Item> ownersItems = itemRepo.findAllByOwnerId(ownerId);
        return ownersItems.stream().map(item -> findItemWithLastAndNextBooking(item.getId(), now, true))
                .collect(Collectors.toList());

    }

    private BookingShortForItem getLastBooking(List<Booking> bookings, LocalDateTime now) {
        Optional<Booking> lastBookingOpt = bookings.stream()
                .filter(b -> b.getEnd().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd));
        if(lastBookingOpt.isEmpty()) {
            return null;
        } else {
            return new BookingShortForItem(lastBookingOpt.get().getId(), lastBookingOpt.get().getBooker().getId());
        }
    }

    private BookingShortForItem getNextBooking(List<Booking> bookings, LocalDateTime now) {
        Optional<Booking> nextBookingOpt = bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart));
        if(nextBookingOpt.isEmpty()) {
            return null;
        } else {
            return new BookingShortForItem(nextBookingOpt.get().getId(), nextBookingOpt.get().getBooker().getId());
        }
    }
}
