package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.dto.ShortBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class JpaBookingRepositoryTest {
    @Autowired
    JpaBookingRepository bookingRepository;
    @Autowired
    JpaUserRepository userRepository;
    @Autowired
    JpaItemRepository itemRepository;

    User userItemsOwner1;
    Item item1Owner1;
    Item item2Owner1;

    User userBooker2;
    Booking booking1Item1Booker2;
    Booking booking2Item2Booker2;

    User userBooker3;
    Booking  booking3Item1Booker3;
    Booking  booking4Item2Booker3;
    Booking booking5Item1Booker3;

    @BeforeEach
    public void setUp() {
        userItemsOwner1 = User.builder().name("userItemsOwner1").email("userItemsOwner1@email.com").build();
        userItemsOwner1 = userRepository.save(userItemsOwner1);

        item1Owner1 = Item.builder()
                .name("item1")
                .description("item1 description")
                .ownerId(userItemsOwner1.getId())
                .available(true)
                .itemRequest(null).build();
        item1Owner1 = itemRepository.save(item1Owner1);

        item2Owner1 = Item.builder()
                .name("item2")
                .description("item2 description")
                .ownerId(userItemsOwner1.getId())
                .available(true)
                .itemRequest(null).build();
        item2Owner1 = itemRepository.save(item2Owner1);

        userBooker2 = User.builder().name("userBooker2").email("userBooker2@email.com").build();
        userBooker2 = userRepository.save(userBooker2);

        booking1Item1Booker2 = Booking.builder()
                .booker(userBooker2)
                .item(item1Owner1)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        booking1Item1Booker2 = bookingRepository.save(booking1Item1Booker2);

        booking2Item2Booker2 = Booking.builder()
                .booker(userBooker2)
                .item(item2Owner1)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();
        booking2Item2Booker2 = bookingRepository.save(booking2Item2Booker2);

        userBooker3 = User.builder().name("userBooker3").email("userBooker3@email.com").build();
        userBooker3 = userRepository.save(userBooker3);

        booking3Item1Booker3 = Booking.builder()
                .booker(userBooker3)
                .item(item1Owner1)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .build();
        booking3Item1Booker3 = bookingRepository.save(booking3Item1Booker3);

        booking4Item2Booker3 = Booking.builder()
                .booker(userBooker3)
                .item(item2Owner1)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().minusDays(3))
                .build();
        booking4Item2Booker3 = bookingRepository.save(booking4Item2Booker3);

        booking5Item1Booker3 = Booking.builder()
                .booker(userBooker3)
                .item(item1Owner1)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(5))
                .end(LocalDateTime.now().plusDays(6))
                .build();
        booking5Item1Booker3 = bookingRepository.save(booking5Item1Booker3);


    }

    @Test
    void getShortBookingsByItemIdWithBookingsStatusApproved() {
        List<ShortBooking> shortBookingList =
                bookingRepository.getShortBookingsByItemId(item1Owner1.getId(), BookingStatus.APPROVED);

        assertEquals(2, shortBookingList.size());
        assertEquals(booking1Item1Booker2.getId(), shortBookingList.get(0).getId());
        assertEquals(booking3Item1Booker3.getId(), shortBookingList.get(1).getId());
    }

    @Test
    void getShortBookingsByItemIdWithBookingsStatusWaiting() {
        List<ShortBooking> shortBookingList =
                bookingRepository.getShortBookingsByItemId(item1Owner1.getId(), BookingStatus.WAITING);

        assertEquals(1, shortBookingList.size());
        assertEquals(booking5Item1Booker3.getId(), shortBookingList.get(0).getId());
    }

    @Test
    void getShortBookingsByItemIdWithoutBookings() {
        bookingRepository.deleteAll();

        List<ShortBooking> shortBookingList =
                bookingRepository.getShortBookingsByItemId(item2Owner1.getId(), BookingStatus.APPROVED);

        assertTrue(shortBookingList.isEmpty());
    }

    @Test
    void getShortBookingsByItemsOwnerIdWithBookingsStatusApproved() {
        List<ShortBooking> result =
                bookingRepository.getShortBookingsByItemsOwnerId(userItemsOwner1.getId(), BookingStatus.APPROVED);

        assertEquals(4, result.size());
    }

    @Test
    void getShortBookingsByItemsOwnerIdWithBookingsStatusWaiting() {
        List<ShortBooking> result =
                bookingRepository.getShortBookingsByItemsOwnerId(userItemsOwner1.getId(), BookingStatus.WAITING);

        assertEquals(1, result.size());
    }

    @Test
    void getShortBookingsByItemsOwnerIdWithoutBookings() {
        bookingRepository.deleteAll();

        List<ShortBooking> result =
                bookingRepository.getShortBookingsByItemsOwnerId(userItemsOwner1.getId(), BookingStatus.WAITING);

        assertTrue(result.isEmpty());
    }

    @AfterEach
    void clear() {
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}