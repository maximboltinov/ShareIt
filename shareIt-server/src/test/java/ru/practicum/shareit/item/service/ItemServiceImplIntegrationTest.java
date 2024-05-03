package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemBookingCommentsResponseDto;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ItemServiceImplIntegrationTest {
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;

    @Autowired
    JpaUserRepository userRepository;
    @Autowired
    JpaItemRepository itemRepository;

    UserResponseDto owner;
    UserResponseDto another;

    @BeforeEach
    public void setUp() {
        owner = userService.create(new UserRequestDto("user1@mail.com", "user1"));
        another = userService.create(new UserRequestDto("user2@mail.com", "user2"));

        itemService.create(owner.getId(),
                CreateItemRequestDto.builder().name("item1").description("item1 description").available(true).build());
        itemService.create(owner.getId(),
                CreateItemRequestDto.builder().name("item2").description("item2 description").available(true).build());
    }

    @Test
    void getByUserId_withItems() {
        List<ItemBookingCommentsResponseDto> result = itemService.getByUserId(owner.getId(), 0L, 5L);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getName()).isEqualTo("item1");
        assertThat(result.get(1).getName()).isEqualTo("item2");
    }

    @Test
    void getByUserId_withoutItems() {
        List<ItemBookingCommentsResponseDto> result = itemService.getByUserId(another.getId(), 0L, 5L);

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    void getByUserId_unknownUser() {
        Assertions.assertThrows(ObjectNotFoundException.class,
                () -> itemService.getByUserId(100500L, 0L, 5L));
    }

    @AfterEach
    public void clear() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }
}