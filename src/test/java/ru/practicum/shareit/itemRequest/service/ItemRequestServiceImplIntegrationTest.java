package ru.practicum.shareit.itemRequest.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.itemRequest.dto.GetItemRequestResponseDto;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class ItemRequestServiceImplIntegrationTest {
    @Autowired
    UserService userService;
    @Autowired
    ItemRequestService itemRequestService;

    @Autowired
    JpaUserRepository userRepository;
    @Autowired
    JpaItemRequestRepository itemRequestRepository;

    UserResponseDto user1;
    UserResponseDto user2;
    UserResponseDto user3;

    @BeforeEach
    public void setUp() {
        user1 = userService.create(new UserRequestDto("user1@mail.com", "user1"));
        user2 = userService.create(new UserRequestDto("user2@mail.com", "user2"));
        user3 = userService.create(new UserRequestDto("user3@mail.com", "user3"));

        itemRequestService.create(user1.getId(), new ItemRequestDto("request1 description"));
        itemRequestService.create(user1.getId(), new ItemRequestDto("request2 description"));
        itemRequestService.create(user2.getId(), new ItemRequestDto("request3 description"));
    }

    @Test
    void getAllRequestsAnotherUsers_withRequests() {
        List<GetItemRequestResponseDto> result =
                itemRequestService.getAllRequestsAnotherUsers(user2.getId(), 0L, 5L);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getDescription()).isEqualTo("request2 description");
        assertThat(result.get(1).getDescription()).isEqualTo("request1 description");
    }

    @Test
    void getAllRequestsAnotherUsers_withoutRequests() {
        userService.delete(user1.getId());
        userService.delete(user2.getId());

        List<GetItemRequestResponseDto> result =
                itemRequestService.getAllRequestsAnotherUsers(user3.getId(), 0L, 5L);

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void getAllRequestsAnotherUsers_unknownUser() {
        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.getAllRequestsAnotherUsers(100500L, 0L, 5L));
    }

    @AfterEach
    public void clear() {
        itemRequestRepository.deleteAll();
        userRepository.deleteAll();
    }
}