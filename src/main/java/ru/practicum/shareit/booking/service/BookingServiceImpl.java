package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
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
import java.util.Comparator;
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
    public List<BookingResponseDto> getBookingsByBookerId(Long bookerId, String state) {
        if (!userService.isPresent(bookerId)) {
            throw new ObjectNotFoundException("не найден пользователь");
        }

        List<Booking> bookingList = bookingRepository.getBookingsByBookerId(bookerId);
        return filteredEndSorted(bookingList, state).stream()
                .map(BookingDtoMapper::mapperToBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getBookingsByOwnerId(Long ownerId, String state) {
        if (!userService.isPresent(ownerId)) {
            throw new ObjectNotFoundException("не найден пользователь");
        }

        List<Booking> bookingList = bookingRepository.getBookingsByItem_OwnerId(ownerId);
        return filteredEndSorted(bookingList, state).stream()
                .map(BookingDtoMapper::mapperToBookingResponseDto)
                .collect(Collectors.toList());
    }

    private List<Booking> filteredEndSorted(List<Booking> bookingList, String state) {
        List<Booking> outBookingList;

        switch (state) {
            case "ALL":
                outBookingList = bookingList.stream()
                        .sorted(Comparator.comparing(Booking::getId).reversed())
                        .collect(Collectors.toList());
                break;
            case "CURRENT":
                outBookingList = bookingList.stream()
                        .filter(entity -> entity.getStart().isBefore(LocalDateTime.now())
                                && entity.getEnd().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getId))
                        .collect(Collectors.toList());
                break;
            case "PAST"://завершенные
                outBookingList = bookingList.stream()
                        .filter(entity -> entity.getEnd().isBefore(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getId).reversed())
                        .collect(Collectors.toList());
                break;
            case "FUTURE"://будущие
                outBookingList = bookingList.stream()
                        .filter(entity -> entity.getStart().isAfter(LocalDateTime.now()))
                        .sorted(Comparator.comparing(Booking::getId).reversed())
                        .collect(Collectors.toList());
                break;
            case "WAITING"://ожидающие подтверждения
                outBookingList = bookingList.stream()
                        .filter(entity -> entity.getStatus().equals(BookingStatus.WAITING))
                        .sorted(Comparator.comparing(Booking::getId).reversed())
                        .collect(Collectors.toList());
                break;
            case "REJECTED"://отклоненные
                outBookingList = bookingList.stream()
                        .filter(entity -> entity.getStatus().equals(BookingStatus.REJECTED))
                        .sorted(Comparator.comparing(Booking::getId).reversed())
                        .collect(Collectors.toList());
                break;
            default:
                throw new BadRequestException("error", "Unknown state: UNSUPPORTED_STATUS");
        }

        return outBookingList;
    }
}