package ru.practicum.shareit.item.projection;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.projection.BookingShortForItem;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithLastAndNextBookingAndComments {
    Long id;
    Long ownerId;
    String name;
    String description;
    Boolean available;
    BookingShortForItem lastBooking;
    BookingShortForItem nextBooking;
    List<Comment> comments;
}
