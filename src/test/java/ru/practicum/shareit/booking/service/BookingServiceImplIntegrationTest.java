package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemOnlyResponseDto;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BookingServiceImplIntegrationTest {
    @Autowired
    BookingService bookingService;
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;

    @Autowired
    JpaUserRepository userRepository;
    @Autowired
    JpaItemRepository itemRepository;
    @Autowired
    JpaBookingRepository bookingRepository;

    private UserResponseDto owner;
    private UserResponseDto tenant;

    @BeforeEach
    public void setUp() {
        owner = userService.create(new UserRequestDto("user1@mail.com", "user1"));
        tenant = userService.create(new UserRequestDto("user2@mail.com", "user2"));

        ItemOnlyResponseDto item1 = itemService.create(owner.getId(),
                CreateItemRequestDto.builder().name("item1").description("item1 description").available(true).build());
        ItemOnlyResponseDto item2 = itemService.create(owner.getId(),
                CreateItemRequestDto.builder().name("item2").description("item2 description").available(true).build());

        bookingService.create(tenant.getId(),
                new BookingRequestDto(item1.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
        bookingService.create(tenant.getId(),
                new BookingRequestDto(item2.getId(), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
    }

    @Test
    void getBookingsByOwnerId_withBookings() {
        List<BookingResponseDto> result =
                bookingService.getBookingsByOwnerId(owner.getId(), "ALL", 0L, 5L);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getItem().getName()).isEqualTo("item2");
        assertThat(result.get(1).getItem().getName()).isEqualTo("item1");
    }

    @Test
    void getBookingsByOwnerId_withoutBookings() {
        List<BookingResponseDto> result =
                bookingService.getBookingsByOwnerId(tenant.getId(), "ALL", 0L, 5L);

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void getBookingsByOwnerId_unknownUser() {
        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingsByOwnerId(100500L, "ALL", 0L, 5L));
    }

    @AfterEach
    public void clear() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}