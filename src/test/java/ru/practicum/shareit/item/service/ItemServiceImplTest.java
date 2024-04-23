package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dto.ShortBooking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaCommentRepository;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    ItemService itemService;
    @Mock
    UserService userService;
    @Mock
    JpaBookingRepository bookingRepository;
    @Mock
    JpaItemRepository itemRepository;
    @Mock
    JpaCommentRepository commentRepository;
    @Mock
    JpaItemRequestRepository itemRequestRepository;

    @BeforeEach
    public void setUp() {
        itemService = new ItemServiceImpl(userService,
                bookingRepository,
                itemRepository,
                commentRepository,
                itemRequestRepository);
    }

    @Test
    void createOwnerIdIsNull() {
        when(userService.getUserById(null))
                .thenThrow(new BadRequestException("", "id не может быть null"));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.create(null, CreateItemRequestDto.builder().build()));

        assertEquals("id не может быть null", exception.getMessage());
    }

    @Test
    void createNoOwnerById() {
        when(userService.getUserById(anyLong()))
                .thenThrow(new ObjectNotFoundException("пользователь не найден"));

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.create(1L, CreateItemRequestDto.builder().build()));

        verify(userService, times(1))
                .getUserById(1L);

        assertEquals("пользователь не найден", exception.getMessage());
    }

    @Test
    void createNoRequestById() {
        when(userService.getUserById(anyLong()))
                .thenReturn(new User());

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.create(1L, CreateItemRequestDto.builder().requestId(1L).build()));

        verify(itemRequestRepository, times(1))
                .findById(1L);

        assertEquals("запрос на добавление вещи не найден", exception.getMessage());
    }

    @Test
    void createCorrect() {
        LocalDateTime localDateTime = LocalDateTime.now();

        when(userService.getUserById(anyLong()))
                .thenReturn(new User());

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(new ItemRequest(1L, "dddd", localDateTime, new User())));

        when(itemRepository.save(any(Item.class)))
                .thenReturn(Item.builder()
                        .id(1L)
                        .name("some")
                        .description("somesome")
                        .available(true)
                        .ownerId(1L)
                        .itemRequest(new ItemRequest(1L, "dddd", localDateTime, new User()))
                        .build());


        ItemOnlyResponseDto itemSaved = itemService.create(1L,
                CreateItemRequestDto.builder()
                        .name("some")
                        .description("somesome")
                        .available(true)
                        .requestId(1L)
                        .build());

        verify(userService, times(1))
                .getUserById(1L);
        verify(itemRequestRepository, times(1))
                .findById(1L);
        verify(itemRepository, times(1))
                .save(Item.builder()
                        .id(null)
                        .name("some")
                        .description("somesome")
                        .available(true)
                        .ownerId(1L)
                        .itemRequest(new ItemRequest(1L, "dddd", localDateTime, new User()))
                        .build());

        assertEquals(1L, itemSaved.getId());
        assertEquals("some", itemSaved.getName());
        assertEquals("somesome", itemSaved.getDescription());
        assertEquals(1L, itemSaved.getRequestId());
        assertEquals(true, itemSaved.getAvailable());
    }

    @Test
    void updateOwnerIdNull() {
        when(userService.getUserById(null))
                .thenThrow(new BadRequestException("", "пользователь не может быть null"));

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.update(null, 1L,
                        new UpdateItemRequestDto(1L, "jjj", "fff", true)));

        verify(userService, times(1))
                .getUserById(null);

        assertEquals("пользователь не может быть null", exception.getMessage());
    }

    @Test
    void updateWithNotOwnerById() {
        when(userService.getUserById(anyLong()))
                .thenThrow(new ObjectNotFoundException("пользователь не найден"));

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(1L, 1L,
                        new UpdateItemRequestDto(1L, "jjj", "fff", true)));

        verify(userService, times(1))
                .getUserById(1L);

        assertEquals("пользователь не найден", exception.getMessage());
    }

    @Test
    void updateItemIdNull() {
        when(userService.getUserById(anyLong()))
                .thenReturn(new User());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.update(1L, null,
                        new UpdateItemRequestDto(1L, "jjj", "fff", true)));

        assertEquals("itemId не может быть null", exception.getMessage());
    }

    @Test
    void updateWithNotItemById() {
        when(userService.getUserById(anyLong()))
                .thenReturn(new User());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(1L, 1L,
                        new UpdateItemRequestDto(1L, "jjj", "fff", true)));

        assertEquals("Вещь с id 1 не найдена", exception.getMessage());
    }

    @Test
    void updateWithUpdateItemNull() {
        when(userService.getUserById(anyLong()))
                .thenReturn(new User());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.update(1L, 1L, null));

        assertEquals("updateItem не может быть null", exception.getMessage());
    }

    @Test
    void updateWithUserIdNotEqualsOwnerId() {
        when(userService.getUserById(anyLong()))
                .thenReturn(new User());
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(Item.builder().ownerId(2L).build()));

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(1L, 1L,
                        new UpdateItemRequestDto(1L, "", "", true)));

        assertEquals("Несоответствие id владельца", exception.getMessage());
    }

    @Test
    void updateCorrect() {
        when(userService.getUserById(anyLong()))
                .thenReturn(new User());
        when(itemRepository.findById(1L))
                .thenReturn(Optional.of(Item.builder()
                        .id(1L)
                        .name("item")
                        .description("description")
                        .available(true)
                        .ownerId(1L)
                        .build()));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(Item.builder()
                        .id(1L)
                        .ownerId(1L)
                        .available(false)
                        .name("updateItem")
                        .description("UpdateDescription")
                        .itemRequest(null)
                        .build());

        ItemOnlyResponseDto itemAnswer = itemService.update(1L, 1L,
                new UpdateItemRequestDto(1L, "updateItem", "UpdateDescription", false));

        verify(userService).getUserById(1L);
        verify(itemRepository).findById(1L);
        verify(itemRepository).save(Item.builder()
                .id(1L)
                .ownerId(1L)
                .available(false)
                .name("updateItem")
                .description("UpdateDescription")
                .itemRequest(null)
                .build());

        assertEquals(1L, itemAnswer.getId());
        assertEquals("updateItem", itemAnswer.getName());
        assertEquals("UpdateDescription", itemAnswer.getDescription());
        assertEquals(false, itemAnswer.getAvailable());
        assertNull(itemAnswer.getRequestId());
    }

    @Test
    void getByItemIdWithUserIdNull() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.getByItemId(1L, null));

        assertEquals("userId не может быть null", exception.getMessage());
    }

    @Test
    void getByItemIdWithNoUserById() {
        when(userService.isPresent(anyLong()))
                .thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getByItemId(1L, 1L));

        verify(userService).isPresent(1L);

        assertEquals("пользователь не найден", exception.getMessage());
    }

    @Test
    void getByItemIdCorrectWithUserIdNotEqualsItemOwnerIdAndNoComments() {
        when(userService.isPresent(anyLong()))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(Item.builder()
                        .id(1L)
                        .name("some")
                        .description("somesome")
                        .available(true)
                        .ownerId(2L)
                        .itemRequest(new ItemRequest())
                        .build()));
        when(commentRepository.getCommentsOutDtoByItemId(anyLong()))
                .thenReturn(Optional.empty());

        ItemBookingCommentsResponseDto itemAnswer = itemService.getByItemId(1L, 3L);

        verify(userService).isPresent(3L);
        verify(itemRepository).findById(1L);
        verify(commentRepository).getCommentsOutDtoByItemId(1L);

        assertEquals(1L, itemAnswer.getId());
        assertEquals("some", itemAnswer.getName());
        assertEquals("somesome", itemAnswer.getDescription());
        assertEquals(true, itemAnswer.getAvailable());
        assertNull(itemAnswer.getLastBooking());
        assertNull(itemAnswer.getNextBooking());
        assertEquals(List.of(), itemAnswer.getComments());
    }

    @Test
    void getByItemIdCorrectWithUserIdEqualsItemOwnerIdAndComments() {
        when(userService.isPresent(anyLong()))
                .thenReturn(true);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(Item.builder()
                        .id(1L)
                        .name("some")
                        .description("somesome")
                        .available(true)
                        .ownerId(2L)
                        .itemRequest(new ItemRequest())
                        .build()));
        when(commentRepository.getCommentsOutDtoByItemId(anyLong()))
                .thenReturn(
                        Optional.of(
                                List.of(CommentResponseDto.builder().build(), CommentResponseDto.builder().build())));

        ShortBooking booking1 = new ShortBooking(1L, 5L,
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(9),
                1L);
        ShortBooking booking2 = new ShortBooking(2L, 6L,
                LocalDateTime.now().minusDays(8),
                LocalDateTime.now().minusDays(7),
                1L);
        ShortBooking booking3 = new ShortBooking(3L, 7L,
                LocalDateTime.now().plusDays(7),
                LocalDateTime.now().minusDays(8),
                1L);
        ShortBooking booking4 = new ShortBooking(4L, 8L,
                LocalDateTime.now().plusDays(9),
                LocalDateTime.now().minusDays(10),
                1L);

        when(bookingRepository.getShortBookingsByItemId(anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(booking1, booking2, booking3, booking4));

        ItemBookingCommentsResponseDto itemAnswer = itemService.getByItemId(1L, 2L);

        verify(userService).isPresent(2L);
        verify(itemRepository).findById(1L);
        verify(commentRepository).getCommentsOutDtoByItemId(1L);
        verify(bookingRepository).getShortBookingsByItemId(1L, BookingStatus.APPROVED);

        assertEquals(1L, itemAnswer.getId());
        assertEquals("some", itemAnswer.getName());
        assertEquals("somesome", itemAnswer.getDescription());
        assertEquals(true, itemAnswer.getAvailable());
        assertEquals(booking2, itemAnswer.getLastBooking());
        assertEquals(booking3, itemAnswer.getNextBooking());
        assertEquals(2, itemAnswer.getComments().size());
    }

    @Test
    void getByUserIdWithIncorrectPageParameters() {
        when(userService.getUserById(anyLong()))
                .thenReturn(new User());

        BadRequestException exception1 = assertThrows(BadRequestException.class,
                () -> itemService.getByUserId(1L, -1L, 2L));

        assertEquals("некорректные параметры страницы", exception1.getMessage());

        BadRequestException exception2 = assertThrows(BadRequestException.class,
                () -> itemService.getByUserId(1L, 0L, 0L));

        assertEquals("некорректные параметры страницы", exception2.getMessage());

        verify(userService, times(2)).getUserById(1L);
    }

    @Test
    void getByUserIdCorrect() {
        when(userService.getUserById(anyLong()))
                .thenReturn(new User());

        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "id"));
        List<Item> forPage = List.of(
                Item.builder()
                        .id(1L)
                        .name("some")
                        .description("somesome")
                        .available(true)
                        .ownerId(1L)
                        .itemRequest(new ItemRequest())
                        .build());

        when(itemRepository.findItemByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(forPage, pageable, forPage.size()));

        when(bookingRepository.getShortBookingsByItemsOwnerId(1L, BookingStatus.APPROVED))
                .thenReturn(List.of(
                        new ShortBooking(2L, 3L,
                                LocalDateTime.now().plusDays(1),
                                LocalDateTime.now().plusDays(2),
                                1L)));


        List<ItemBookingCommentsResponseDto> items = itemService.getByUserId(1L, 0L, 1L);

        verify(userService).getUserById(1L);
        verify(itemRepository).findItemByOwnerId(1L, pageable);
        verify(bookingRepository).getShortBookingsByItemsOwnerId(1L, BookingStatus.APPROVED);

        assertEquals(1, items.size());
        assertEquals(1, items.get(0).getId());
        assertEquals("some", items.get(0).getName());
        assertEquals("somesome", items.get(0).getDescription());
        assertNull(items.get(0).getLastBooking());
        assertNotNull(items.get(0).getNextBooking());
        assertNull(items.get(0).getComments());
        assertEquals(true, items.get(0).getAvailable());
    }

    @Test
    void searchByTextWithEmptyTextForSearch() {
        assertEquals(List.of(), itemService.searchByText("", 0L, 1L));
    }

    @Test
    void searchByTextWithIncorrectPageParameters() {
        BadRequestException exception1 = assertThrows(BadRequestException.class,
                () -> itemService.searchByText("sss", -1L, 1L));

        assertEquals("некорректные параметры страницы", exception1.getMessage());

        BadRequestException exception2 = assertThrows(BadRequestException.class,
                () -> itemService.searchByText("sss", 0L, 0L));

        assertEquals("некорректные параметры страницы", exception2.getMessage());
    }

    @Test
    void searchByTextCorrect() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "id"));
        Item item1 = Item.builder().id(1L).name("some").description("description").available(true)
                .itemRequest(new ItemRequest(3L, null, null, null))
                .build();
        Item item2 = new Item();

        when(itemRepository.some(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(item1, item2), pageable, 2));

        List<ItemOnlyResponseDto> itemsAnswer = itemService.searchByText("som", 0L, 3L);

        verify(itemRepository).some("som", pageable);

        assertEquals(2, itemsAnswer.size());
        assertEquals(1L, itemsAnswer.get(0).getId());
        assertEquals("some", itemsAnswer.get(0).getName());
        assertEquals("description", itemsAnswer.get(0).getDescription());
        assertEquals(true, itemsAnswer.get(0).getAvailable());
        assertEquals(3L, itemsAnswer.get(0).getRequestId());

    }

    @Test
    void addCommentWithNoUserById() {
        when(userService.isPresent(anyLong()))
                .thenReturn(false);

        CommentRequestDto comment = new CommentRequestDto();
        comment.setText("text");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.addComment(1L, 2L, comment));

        assertEquals("Пользователь не существует", exception.getMessage());
    }

    @Test
    void addCommentWithNoItemById() {
        when(userService.isPresent(anyLong()))
                .thenReturn(true);
        when(itemRepository.existsById(anyLong()))
                .thenReturn(false);

        CommentRequestDto comment = new CommentRequestDto();
        comment.setText("text");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.addComment(1L, 2L, comment));

        assertEquals("Вещь не существует", exception.getMessage());
    }

    @Test
    void addCommentWithNoBooking() {
        when(userService.isPresent(anyLong()))
                .thenReturn(true);
        when(itemRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.
                countApprovedBookingsForUserAndItemAnEarlyEndDate(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        CommentRequestDto comment = new CommentRequestDto();
        comment.setText("text");

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.addComment(1L, 2L, comment));

        assertEquals("Пользователь еще не арендовал эту вещь или аренда еще не закончилась",
                exception.getMessage());
    }

    @Test
    void addCommentCorrect() {
        when(userService.isPresent(anyLong()))
                .thenReturn(true);
        when(itemRepository.existsById(anyLong()))
                .thenReturn(true);
        when(bookingRepository.
                countApprovedBookingsForUserAndItemAnEarlyEndDate(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(Optional.of(1));

        User author = new User();
        author.setName("author");

        when(userService.getUserById(anyLong()))
                .thenReturn(author);

        Item item = Item.builder().id(2L).build();

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(commentRepository.save(any(Comment.class)))
                .thenReturn(Comment.builder().id(1L).text("text").author(author).item(item).build());

        CommentRequestDto commentRequest = new CommentRequestDto();
        commentRequest.setText("text");

        CommentResponseDto commentAnswer = itemService.addComment(1L, 2L, commentRequest);

        verify(userService).isPresent(1L);
        verify(itemRepository).existsById(2L);
        verify(userService).getUserById(1L);
        verify(itemRepository).findById(2L);

        assertEquals(1L, commentAnswer.getId());
        assertEquals("text", commentAnswer.getText());
        assertEquals(2L, commentAnswer.getItemId());
        assertEquals("author", commentAnswer.getAuthorName());
    }
}