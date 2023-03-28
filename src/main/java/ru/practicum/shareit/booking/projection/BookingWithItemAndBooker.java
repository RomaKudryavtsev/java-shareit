package ru.practicum.shareit.booking.projection;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.projection.Booker;

import java.time.Instant;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingWithItemAndBooker {
    Long id;
    Instant start;
    Instant end;
    BookingStatus status;
    Item item;
    Booker booker;
}
