package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.ZoneOffset;

public class BookingMapper {
    public static Booking mapDtoToModel(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart().toInstant(ZoneOffset.UTC));
        booking.setEnd(bookingDto.getEnd().toInstant(ZoneOffset.UTC));
        return booking;
    }
}
