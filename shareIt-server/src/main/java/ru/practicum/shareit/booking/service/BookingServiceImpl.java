package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
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
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final JpaBookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

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

        return BookingDtoMapper.mapperToBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto updateApprove(Long itemOwnerId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BadRequestException("updateApprove", "не найден booking"));

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
    public List<BookingResponseDto> getBookingsByBookerId(Long bookerId, BookingState state, Long from, Long size) {
        if (!userService.isPresent(bookerId)) {
            throw new ObjectNotFoundException("не найден пользователь");
        }

        Pageable pageable = PageRequest.of(Math.toIntExact(from) / Math.toIntExact(size),
                Math.toIntExact(size),
                Sort.by(Sort.Direction.DESC, "start"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = bookingRepository
                        .getBookingByBooker_Id(bookerId, pageable)
                        .getContent();
                break;
            case CURRENT:
                bookings = bookingRepository
                        .getBookingByBooker_IdAndStartBeforeAndEndAfter(bookerId, now, now, pageable)
                        .getContent();
                break;
            case PAST://завершенные
                bookings = bookingRepository
                        .getBookingByBooker_IdAndEndBefore(bookerId, now, pageable)
                        .getContent();
                break;
            case FUTURE://будущие
                bookings = bookingRepository
                        .getBookingByBooker_IdAndStartAfterAndEndAfter(bookerId, now, now, pageable)
                        .getContent();
                break;
            case WAITING://ожидающие подтверждения
                bookings = bookingRepository
                        .getBookingByBooker_IdAndStatus(bookerId, BookingStatus.WAITING, pageable)
                        .getContent();
                break;
            case REJECTED://отклоненные
                bookings = bookingRepository
                        .getBookingByBooker_IdAndStatus(bookerId, BookingStatus.REJECTED, pageable)
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
    public List<BookingResponseDto> getBookingsByOwnerId(Long ownerId, BookingState state, Long from, Long size) {
        if (!userService.isPresent(ownerId)) {
            throw new ObjectNotFoundException("не найден пользователь");
        }

        Pageable pageable = PageRequest.of(Math.toIntExact(from) / Math.toIntExact(size),
                Math.toIntExact(size),
                Sort.by(Sort.Direction.DESC, "start"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                bookings = bookingRepository
                        .getBookingByItem_OwnerId(ownerId, pageable)
                        .getContent();
                break;
            case CURRENT:
                bookings = bookingRepository
                        .getBookingByItem_OwnerIdAndStartBeforeAndEndAfter(ownerId, now, now, pageable)
                        .getContent();
                break;
            case PAST://завершенные
                bookings = bookingRepository
                        .getBookingByItem_OwnerIdAndEndBefore(ownerId, now, pageable)
                        .getContent();
                break;
            case FUTURE://будущие
                bookings = bookingRepository
                        .getBookingByItem_OwnerIdAndStartAfterAndEndAfter(ownerId, now, now, pageable)
                        .getContent();
                break;
            case WAITING://ожидающие подтверждения
                bookings = bookingRepository
                        .getBookingByItem_OwnerIdAndStatus(ownerId, BookingStatus.WAITING, pageable)
                        .getContent();
                break;
            case REJECTED://отклоненные
                bookings = bookingRepository
                        .getBookingByItem_OwnerIdAndStatus(ownerId, BookingStatus.REJECTED, pageable)
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