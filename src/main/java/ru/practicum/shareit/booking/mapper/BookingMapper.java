package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.projection.BookingShort;
import ru.practicum.shareit.item.projection.ItemShort;
import ru.practicum.shareit.user.projection.UserShort;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class BookingMapper {
    public static Booking mapDtoToModel(BookingRequestDto bookingRequestDto) {
        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart().toInstant(ZoneOffset.UTC));
        booking.setEnd(bookingRequestDto.getEnd().toInstant(ZoneOffset.UTC));
        return booking;
    }

    public static BookingResponseDto mapModelToDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(LocalDateTime.ofInstant(booking.getStart(), ZoneId.ofOffset("UTC", ZoneOffset.UTC)))
                .end(LocalDateTime.ofInstant(booking.getEnd(), ZoneId.ofOffset("UTC", ZoneOffset.UTC)))
                .status(booking.getStatus())
                .booker(new UserShort(booking.getBooker().getId()))
                .item(new ItemShort(booking.getItem().getId(), booking.getItem().getName()))
                .build();
    }

    public static BookingResponseDto mapProjectionToDto(BookingShort bookingShort) {
        return BookingResponseDto.builder()
                .id(bookingShort.getId())
                .start(LocalDateTime.ofInstant(bookingShort.getStart(), ZoneId.ofOffset("UTC", ZoneOffset.UTC)))
                .end(LocalDateTime.ofInstant(bookingShort.getEnd(), ZoneId.ofOffset("UTC", ZoneOffset.UTC)))
                .status(bookingShort.getStatus())
                .booker(bookingShort.getBooker())
                .item(bookingShort.getItem())
                .build();
    }
}
