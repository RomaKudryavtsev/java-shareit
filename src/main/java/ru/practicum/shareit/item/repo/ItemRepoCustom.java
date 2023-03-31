package ru.practicum.shareit.item.repo;

import ru.practicum.shareit.item.projection.ItemWithLastAndNextBookingAndComments;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepoCustom {
    ItemWithLastAndNextBookingAndComments findItemWithLastAndNextBookingAndComments(Long itemId, LocalDateTime now,
                                                                                    boolean isOwner);

    List<ItemWithLastAndNextBookingAndComments> findAllWithLastAndNextBookingAndComments(Long ownerId,
                                                                                         LocalDateTime now);
}
