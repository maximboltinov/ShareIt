package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
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
    public BookingResponseDto create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                     @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        log.info("Запрос POST /bookings от пользователя id = {} {}", bookerId, bookingRequestDto);
        BookingResponseDto booking = bookingService.create(bookerId, bookingRequestDto);
        log.info("Отправлен ответ POST /bookings {}", booking);
        return booking;
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto update(@RequestHeader("X-Sharer-User-Id") Long itemOwnerId,
                                     @PathVariable Long bookingId,
                                     @RequestParam Boolean approved) {
        log.info("Запрос PATCH /bookings/{}?approved={}", bookingId, approved);
        BookingResponseDto booking = bookingService.updateApprove(itemOwnerId, bookingId, approved);
        log.info("отправлен ответ PATCH /bookings/{}?approved={} {}", bookingId, approved, booking);
        return booking;
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Запрос GET /bookings/{}", bookingId);
        BookingResponseDto booking = bookingService.getBookingById(userId, bookingId);
        log.info("Отправлен ответ GET /bookings/{} {}", bookingId, booking);
        return booking;
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                                          @RequestParam(defaultValue = "ALL") String state,
                                                          @RequestParam(defaultValue = "0") Long from,
                                                          @RequestParam(defaultValue = "20") Long size) {
        log.info("Запрос GET /bookings?state={}&from={}&size={}", state, from, size);
        List<BookingResponseDto> bookingList = bookingService.getBookingsByBookerId(bookerId, state, from, size);
        log.info("Отправлен ответ GET /bookings?state={}&from={}&size={} {}", state, from, size, bookingList);
        return bookingList;
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                         @RequestParam(defaultValue = "ALL") String state) {
        log.info("Запрос GET /bookings/owner?state={}", state);
        List<BookingResponseDto> bookingList = bookingService.getBookingsByOwnerId(ownerId, state);
        log.info("Запрос GET /bookings/owner?state={} {}", state, bookingList);
        return bookingList;
    }
}