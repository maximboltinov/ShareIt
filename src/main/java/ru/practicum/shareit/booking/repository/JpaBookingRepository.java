package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.dto.ShortBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaBookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> getBookingsByBookerId(Long bookerId);

    List<Booking> getBookingsByItem_OwnerId(Long ownerId);

    @Query("select new ru.practicum.shareit.booking.dto.ShortBooking( " +
            "booking.id, booker.id, booking.start, booking.end, item.ownerId) " +
            "from Booking booking " +
            "join booking.item item " +
            "join booking.booker booker " +
            "where item.id = ?1 and booking.status = ?2")
    List<ShortBooking> getShortBookingsByItemId(Long itemId, BookingStatus bookingStatus);

    @Query("select new ru.practicum.shareit.booking.dto.ShortBooking(" +
            "booking.id, booker.id, booking.start, booking.end, item.id) " +
            "from Booking booking " +
            "join booking.item item " +
            "join booking.booker booker " +
            "where item.ownerId = ?1 and booking.status = ?2")
    List<ShortBooking> getShortBookingsByItemsOwnerId(Long ownerId, BookingStatus bookingStatus);

@Query("select count(booking.id) from Booking booking " +
        "join booking.item item " +
        "join booking.booker booker " +
        "where booker.id = ?1 and item.id = ?2 and booking.end < ?3 and booking.status = 'APPROVED' " +
        "group by booking.id")
    Optional<Integer> countApprovedBookingsForUserAndItemAnEarlyEndDate(Long authorId, Long itemId, LocalDateTime date);
}