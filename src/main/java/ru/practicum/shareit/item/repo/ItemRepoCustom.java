package ru.practicum.shareit.item.repo;

import ru.practicum.shareit.item.projection.ItemWithLastAndNextBooking;

import java.time.Instant;
import java.util.List;

public interface ItemRepoCustom {
    ItemWithLastAndNextBooking findItemWithLastAndNextBooking (Long itemId, Instant now, boolean isOwner);

    List<ItemWithLastAndNextBooking> findAllWithLastAndNextBooking (Long ownerId, Instant now);
}
