package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

public final class BookingDtoMapper {
    private BookingDtoMapper() {
    }

    public static Booking mapperToBooking(BookingRequestDto bookingRequestDto) {
        return Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status(BookingStatus.WAITING)
                .build();
    }

    public static BookingResponseDto mapperToBookingResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(new BookingResponseDto.BookerId(booking.getBooker().getId()))
                .item(new BookingResponseDto.ItemIdName(booking.getItem().getId()
                        , booking.getItem().getName()))
                .build();
    }
}