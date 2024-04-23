package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    BookingService bookingService;

    @Mock
    JpaBookingRepository bookingRepository;
    @Mock
    ItemService itemService;
    @Mock
    UserService userService;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, itemService, userService);
    }

    @Test
    void createWithBookerEqualsItemOwner() {
        User user = new User(1L, "user@mail.com", "user");
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Item item =
                new Item(2L, "item", "item description", true, 1L, null);
        when(itemService.getItemById(anyLong()))
                .thenReturn(item);

        BookingRequestDto bookingRequestDto =
                new BookingRequestDto(2L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3));

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.create(1L, bookingRequestDto));

        assertEquals("нельзя арендовать у себя", exception.getMessage());
    }

    @Test
    void createWithItemNotAvailable() {
        User user = new User(3L, "user@mail.com", "user");
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Item item =
                new Item(2L, "item", "item description", false, 1L, null);
        when(itemService.getItemById(anyLong()))
                .thenReturn(item);

        BookingRequestDto bookingRequestDto =
                new BookingRequestDto(2L, LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.create(3L, bookingRequestDto));

        assertEquals("item недоступен", exception.getMessage());
    }

    @Test
    void createWithBadDate() {
        User user = new User(3L, "user@mail.com", "user");
        when(userService.getUserById(anyLong()))
                .thenReturn(user);
        Item item =
                new Item(2L, "item", "item description", true, 1L, null);
        when(itemService.getItemById(anyLong()))
                .thenReturn(item);

        BookingRequestDto bookingRequestDto =
                new BookingRequestDto(2L, LocalDateTime.now().minusDays(2), LocalDateTime.now().plusDays(3));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.create(3L, bookingRequestDto));

        assertEquals("дата окончания или начала в прошлом", exception.getMessage());

        bookingRequestDto.setStart(LocalDateTime.now().plusDays(3));
        bookingRequestDto.setEnd(bookingRequestDto.getStart());

        BadRequestException exception1 = assertThrows(BadRequestException.class,
                () -> bookingService.create(3L, bookingRequestDto));

        assertEquals("дата окончания раньше начала или равны", exception1.getMessage());
    }

    @Test
    void createCorrect() {
        User user = new User(3L, "user@mail.com", "user");

        when(userService.getUserById(anyLong()))
                .thenReturn(user);

        Item item =
                new Item(2L, "item", "item description", true, 1L, null);

        when(itemService.getItemById(anyLong()))
                .thenReturn(item);

        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = start.plusDays(1);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(new Booking(1L, start, end, BookingStatus.WAITING, item, user));

        BookingRequestDto bookingRequestDto =
                new BookingRequestDto(2L, start, end);

        BookingResponseDto bookingAnswer = bookingService.create(3L, bookingRequestDto);

        verify(bookingRepository).save(new Booking(null, start, end, BookingStatus.WAITING, item, user));

        assertEquals(1L, bookingAnswer.getId());
        assertEquals("item", bookingAnswer.getItem().getName());
        assertEquals(3L, bookingAnswer.getBooker().getId());
    }

    @Test
    void updateApproveWithNoBookingById() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.updateApprove(1L, 1L, true));

        assertEquals("не найден booking", exception.getMessage());
    }

    @Test
    void updateApproveStatusChangeAfterApproval() {
        Booking booking = Booking.builder().status(BookingStatus.APPROVED).build();
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.updateApprove(1L, 1L, true));

        assertEquals("изменение статуса после согласования", exception.getMessage());
    }

    @Test
    void updateApproveUserNotOwner() {
        Booking booking = Booking.builder().status(BookingStatus.WAITING)
                .item(Item.builder().ownerId(2L).build()).build();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.updateApprove(1L, 1L, true));

        assertEquals("несоответствие пользователя и владельца", exception.getMessage());
    }

    @Test
    void updateApproveCorrect() {
        Booking booking = Booking.builder().status(BookingStatus.WAITING)
                .item(Item.builder().ownerId(1L).build()).build();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        Booking bookingApproved = booking.toBuilder().status(BookingStatus.APPROVED).build();
        bookingApproved.setBooker(new User(1L, null, "NAME"));
        bookingApproved.setItem(Item.builder().id(2L).name("name").build());

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingApproved);

        BookingResponseDto bookingResponseDto = bookingService.updateApprove(1L, 1L, true);

        verify(bookingRepository).save(booking);

        assertEquals(BookingStatus.APPROVED, bookingResponseDto.getStatus());
    }

    @Test
    void getBookingByIdWithNoUserById() {
        when(userService.isPresent(anyLong()))
                .thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(1L, 1L));

        assertEquals("не найден user или booking", exception.getMessage());
    }

    @Test
    void getBookingByIdWithNoBookingById() {
        when(userService.isPresent(anyLong()))
                .thenReturn(true);
        when(bookingRepository.existsById(anyLong()))
                .thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(1L, 1L));

        assertEquals("не найден user или booking", exception.getMessage());
    }

    @Test
    void getBookingByIdWithUserNotEqualsTenantOrOwner() {
        when(userService.isPresent(anyLong()))
                .thenReturn(true);
        when(bookingRepository.existsById(anyLong()))
                .thenReturn(true);

        Booking booking = Booking.builder()
                .id(1L)
                .item(Item.builder().ownerId(3L).build())
                .booker(User.builder().id(2L).build())
                .build();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(1L, 1L));

        assertEquals("несоответствие id владельца вещи или арендатора", exception.getMessage());
    }

    @Test
    void getBookingByIdCorrect() {
        when(userService.isPresent(anyLong()))
                .thenReturn(true);
        when(bookingRepository.existsById(anyLong()))
                .thenReturn(true);

        Booking booking = Booking.builder()
                .id(1L)
                .item(Item.builder().ownerId(1L).build())
                .booker(User.builder().id(2L).build())
                .build();

        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(booking));

        bookingService.getBookingById(1L, 1L);
    }

    @Test
    void getBookingsByBookerId() {
    }

    @Test
    void getBookingsByOwnerId() {
    }
}