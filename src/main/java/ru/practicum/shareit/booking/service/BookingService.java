package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto create(Long tenantId, BookingRequestDto bookingRequestDto);

    BookingResponseDto updateApprove(Long itemOwnerId, Long bookingId, Boolean approved);

    BookingResponseDto getBookingById(Long userId, Long bookingId);

    List<BookingResponseDto> getBookingsByBookerId(Long bookerId, String state, Long from, Long size);

    List<BookingResponseDto> getBookingsByOwnerId(Long ownerId, String state);
}