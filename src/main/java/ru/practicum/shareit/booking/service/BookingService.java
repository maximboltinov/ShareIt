package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(Long tenantId, BookingDto bookingDto);

    Booking updateApprove(Long itemOwnerId, Long bookingId, Boolean approved);

    Booking getBookingById(Long userId, Long bookingId);

    public List<Booking> getBookingsByBookerId(Long bookerId, String state);

    public List<Booking> getBookingsByOwnerId(Long ownerId, String state);
}