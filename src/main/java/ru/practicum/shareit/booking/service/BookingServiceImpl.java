package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final JpaBookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    private final JpaBookingRepository jpaBookingRepository;

    @Override
    public BookingResponseDto create(Long bookerId, BookingRequestDto bookingRequestDto) {
        User userById = userService.getUserById(bookerId);
        Item itemById = itemService.getItemById(bookingRequestDto.getItemId());

        Booking booking = BookingDtoMapper.mapperToBooking(bookingRequestDto, userById, itemById);

        if (Objects.equals(bookerId, booking.getItem().getOwnerId())) {
            throw new ObjectNotFoundException("нельзя арендовать у себя");
        }

        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("booking create", "item недоступен");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now()) || booking.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("booking create", "дата окончания или начала в прошлом");
        }
        if (booking.getEnd().isBefore(booking.getStart()) || booking.getEnd().isEqual(booking.getStart())) {
            throw new BadRequestException("booking create", "дата окончания раньше начала или равны");
        }

        return BookingDtoMapper.mapperToBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto updateApprove(Long itemOwnerId, Long bookingId, Boolean approved) {
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        Booking booking;
        if (optionalBooking.isPresent()) {
            booking = optionalBooking.get();
        } else {
            throw new BadRequestException("updateApprove", "не найден booking");
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new BadRequestException("updateApprove", "изменение статуса после согласования");
        }

        if (Objects.equals(itemOwnerId, booking.getItem().getOwnerId())) {
            if (approved) {
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
        } else {
            throw new ObjectNotFoundException("несоответствие пользователя и владельца");
        }

        return BookingDtoMapper.mapperToBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getBookingById(Long userId, Long bookingId) {
        if (!(userService.isPresent(userId) && bookingRepository.existsById(bookingId))) {
            throw new ObjectNotFoundException("не найден user или booking");
        }

        Booking booking = bookingRepository.findById(bookingId).get();

        if (Objects.equals(booking.getBooker().getId(), userId)
                || Objects.equals(booking.getItem().getOwnerId(), userId)) {
            return BookingDtoMapper.mapperToBookingResponseDto(booking);
        } else {
            throw new ObjectNotFoundException("несоответствие id владельца вещи или арендатора");
        }
    }

    @Override
    public List<BookingResponseDto> getBookingsByBookerId(Long bookerId, String state, Long from, Long size) {
        if (!userService.isPresent(bookerId)) {
            throw new ObjectNotFoundException("не найден пользователь");
        }

        if (from < 0 || size <= 0) {
            throw new BadRequestException("getBookingsByBookerId", "некорректные параметры страницы");
        }

        Pageable pageable = PageRequest.of(Math.toIntExact(from) / Math.toIntExact(size),
                Math.toIntExact(size),
                Sort.by(Sort.Direction.DESC, "start"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case "ALL":
                bookings = jpaBookingRepository
                        .getBookingByBooker_Id(bookerId, pageable)
                        .getContent();
                break;
            case "CURRENT":
                bookings = jpaBookingRepository
                        .getBookingByBooker_IdAndStartBeforeAndEndAfter(bookerId, now, now, pageable)
                        .getContent();
                break;
            case "PAST"://завершенные
                bookings = jpaBookingRepository
                        .getBookingByBooker_IdAndEndBefore(bookerId, now, pageable)
                        .getContent();
                break;
            case "FUTURE"://будущие
                bookings = jpaBookingRepository
                        .getBookingByBooker_IdAndStartAfterAndEndAfter(bookerId, now, now, pageable)
                        .getContent();
                break;
            case "WAITING"://ожидающие подтверждения
                bookings = jpaBookingRepository.
                        getBookingByBooker_IdAndStatus(bookerId, BookingStatus.WAITING, pageable)
                        .getContent();
                break;
            case "REJECTED"://отклоненные
                bookings = jpaBookingRepository.
                        getBookingByBooker_IdAndStatus(bookerId, BookingStatus.REJECTED, pageable)
                        .getContent();
                break;
            default:
                throw new BadRequestException("error", "Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream()
                .map(BookingDtoMapper::mapperToBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwnerId(Long ownerId, String state, Long from, Long size) {
        if (!userService.isPresent(ownerId)) {
            throw new ObjectNotFoundException("не найден пользователь");
        }

        if (from < 0 || size <= 0) {
            throw new BadRequestException("getBookingsByBookerId", "некорректные параметры страницы");
        }

        Pageable pageable = PageRequest.of(Math.toIntExact(from) / Math.toIntExact(size),
                Math.toIntExact(size),
                Sort.by(Sort.Direction.DESC, "start"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case "ALL":
                bookings = jpaBookingRepository
                        .getBookingByItem_OwnerId(ownerId, pageable)
                        .getContent();
                break;
            case "CURRENT":
                bookings = jpaBookingRepository
                        .getBookingByItem_OwnerIdAndStartBeforeAndEndAfter(ownerId, now, now, pageable)
                        .getContent();
                break;
            case "PAST"://завершенные
                bookings = jpaBookingRepository
                        .getBookingByItem_OwnerIdAndEndBefore(ownerId, now, pageable)
                        .getContent();
                break;
            case "FUTURE"://будущие
                bookings = jpaBookingRepository
                        .getBookingByItem_OwnerIdAndStartAfterAndEndAfter(ownerId, now, now, pageable)
                        .getContent();
                break;
            case "WAITING"://ожидающие подтверждения
                bookings = jpaBookingRepository.
                        getBookingByItem_OwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageable)
                        .getContent();
                break;
            case "REJECTED"://отклоненные
                bookings = jpaBookingRepository.
                        getBookingByItem_OwnerIdAndStatus(ownerId, BookingStatus.REJECTED, pageable)
                        .getContent();
                break;
            default:
                throw new BadRequestException("error", "Unknown state: UNSUPPORTED_STATUS");
        }

        return bookings.stream()
                .map(BookingDtoMapper::mapperToBookingResponseDto)
                .collect(Collectors.toList());
    }
}