package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.projection.BookingFull;

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
                .booker(booking.getBooker())
                .item(booking.getItem())
                .build();
    }

    public static BookingResponseDto mapProjectionToDto(BookingFull bookingFull) {
        return BookingResponseDto.builder()
                .id(bookingFull.getId())
                .start(LocalDateTime.ofInstant(bookingFull.getStart(), ZoneId.ofOffset("UTC", ZoneOffset.UTC)))
                .end(LocalDateTime.ofInstant(bookingFull.getEnd(), ZoneId.ofOffset("UTC", ZoneOffset.UTC)))
                .status(bookingFull.getStatus())
                .booker(bookingFull.getBooker())
                .item(bookingFull.getItem())
                .build();
    }
}
