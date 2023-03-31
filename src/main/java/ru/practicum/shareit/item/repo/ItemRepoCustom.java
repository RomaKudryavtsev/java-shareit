package ru.practicum.shareit.item.repo;

import ru.practicum.shareit.item.projection.ItemWithLastAndNextBooking;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepoCustom {
    ItemWithLastAndNextBooking findItemWithLastAndNextBooking (Long itemId, LocalDateTime now, boolean isOwner);

    List<ItemWithLastAndNextBooking> findAllWithLastAndNextBooking (Long ownerId, LocalDateTime now);
}
