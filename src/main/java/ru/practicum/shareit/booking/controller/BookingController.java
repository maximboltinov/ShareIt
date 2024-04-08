package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader("X-Sharer-User-Id") Long bookerId, @Valid @RequestBody BookingDto bookingDto) {
        log.info("Запрос POST /bookings от пользователя id = {} {}", bookerId, bookingDto);
        Booking booking = bookingService.create(bookerId, bookingDto);
        log.info("Отправлен ответ POST /bookings {}", booking);
        return booking;
    }

    @PatchMapping("/{bookingId}")
    public Booking update(@RequestHeader("X-Sharer-User-Id") Long itemOwnerId,
                          @PathVariable Long bookingId,
                          @RequestParam Boolean approved) {
        log.info("Запрос PATCH /bookings/{}?approved={}", bookingId, approved);
        Booking booking = bookingService.updateApprove(itemOwnerId, bookingId, approved);
        log.info("отправлен ответ PATCH /bookings/{}?approved={} {}", bookingId, approved, booking);
        return booking;
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        log.info("Запрос GET /bookings/{}", bookingId);
        Booking booking = bookingService.getBookingById(userId, bookingId);
        log.info("Отправлен ответ GET /bookings/{} {}", bookingId, booking);
        return booking;
    }

    @GetMapping
    public List<Booking> getBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                               @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос GET /bookings?state={}", state);
        List<Booking> bookingList = bookingService.getBookingsByBookerId(bookerId, state);
        log.info("Запрос GET /bookings?state={} {}", state, bookingList);
        return bookingList;
    }

    @GetMapping("/owner")
    public List<Booking> getBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                              @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос GET /bookings/owner?state={}", state);
        List<Booking> bookingList = bookingService.getBookingsByOwnerId(ownerId, state);
        log.info("Запрос GET /bookings/owner?state={} {}", state, bookingList);
        return bookingList;
    }
}