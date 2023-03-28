package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingAddRequestDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public static Booking mapAddRequestDtoToModel(BookingAddRequestDto bookingDto) {
        Booking booking = new Booking();
        booking.setItemId(bookingDto.getItemId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        return booking;
    }
}
