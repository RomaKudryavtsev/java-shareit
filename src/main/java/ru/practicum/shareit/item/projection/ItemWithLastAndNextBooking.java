package ru.practicum.shareit.item.projection;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.projection.BookingShortForItem;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithLastAndNextBooking {
    Long id;
    Long ownerId;
    String name;
    String description;
    Boolean available;
    BookingShortForItem lastBooking;
    BookingShortForItem nextBooking;
}
