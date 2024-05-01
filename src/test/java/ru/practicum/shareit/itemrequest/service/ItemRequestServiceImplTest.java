package ru.practicum.shareit.itemrequest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemrequest.model.ItemRequest;
import ru.practicum.shareit.itemrequest.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    ItemRequestService itemRequestService;

    @Mock
    JpaUserRepository userRepository;
    @Mock
    JpaItemRequestRepository itemRequestRepository;
    @Mock
    JpaItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        itemRequestService = new ItemRequestServiceImpl(userRepository, itemRequestRepository, itemRepository);
    }

    @Test
    void createWithUserNotFound() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.create(1L, null));

        assertEquals("Пользователь с id = 1 не найден", exception.getMessage());
    }

    @Test
    void createCorrect() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(new User()));

        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(new ItemRequest());

        assertDoesNotThrow(() -> itemRequestService.create(1L, new ItemRequestDto()));
    }

    @Test
    void getUserRequestsWithUserNotFound() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.getUserRequests(1L));

        assertEquals("пользователь не найден", exception.getMessage());
    }

    @Test
    void getUserRequestsCorrect() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findByAuthorIdOrderByCreated(anyLong()))
                .thenReturn(Optional.of(List.of()));
        when(itemRepository.findByItemRequest_Author_IdOrderById(anyLong()))
                .thenReturn(Optional.of(List.of()));

        assertDoesNotThrow(() -> itemRequestService.getUserRequests(1L));
    }

    @Test
    void getAllRequestsAnotherUsersWithIncorrectPageParameters() {
        BadRequestException exception1 = assertThrows(BadRequestException.class,
                () -> itemRequestService.getAllRequestsAnotherUsers(1L, -1L, 1L));
        assertEquals("некорректные параметры страницы", exception1.getMessage());

        BadRequestException exception2 = assertThrows(BadRequestException.class,
                () -> itemRequestService.getAllRequestsAnotherUsers(1L, 0L, 0L));
        assertEquals("некорректные параметры страницы", exception2.getMessage());
    }

    @Test
    void getAllRequestsAnotherUsersWithUserNotFound() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.getAllRequestsAnotherUsers(1L, 0L, 1L));

        assertEquals("не найден пользователь", exception.getMessage());
    }

    @Test
    void getAllRequestsAnotherUsersCorrect() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "created"));

        when(itemRequestRepository.findByAuthorIdNotOrderByCreatedDesc(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), pageable, 0));

        when(itemRepository.findByItemRequest_Author_IdNotOrderById(anyLong()))
                .thenReturn(Optional.of(List.of()));

        assertDoesNotThrow(() -> itemRequestService.getAllRequestsAnotherUsers(1L, 0L, 1L));

        verify(itemRequestRepository).findByAuthorIdNotOrderByCreatedDesc(1L, pageable);
        verify(itemRepository).findByItemRequest_Author_IdNotOrderById(1L);
    }

    @Test
    void getRequestByIdWithUserNotFound() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 1L));

        assertEquals("пользователь не найден", exception.getMessage());
    }

    @Test
    void getRequestByIdWithRequestNotFound() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.getRequestById(1L, 1L));

        assertEquals("запрос не найден", exception.getMessage());
    }

    @Test
    void getRequestByIdCorrect() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(new ItemRequest()));

        when(itemRepository.findItemByItemRequest_IdOrderById(anyLong()))
                .thenReturn(Optional.of(List.of()));

        assertDoesNotThrow(() -> itemRequestService.getRequestById(1L, 1L));

        verify(itemRequestRepository).findById(1L);
        verify(itemRepository).findItemByItemRequest_IdOrderById(1L);
    }
}