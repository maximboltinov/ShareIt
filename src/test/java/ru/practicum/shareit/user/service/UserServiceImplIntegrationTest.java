package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceImplIntegrationTest {
    @Autowired
    UserService userService;

    @Autowired
    JpaUserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userService.create(new UserRequestDto("user1@mail.com", "user1"));
        userService.create(new UserRequestDto("user2@mail.com", "user2"));
        userService.create(new UserRequestDto("user3@mail.com", "user3"));
    }

    @Test
    void getAll_withUsers() {
        List<UserResponseDto> result = userService.getAll();

        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0).getName()).isEqualTo("user1");
        assertThat(result.get(1).getName()).isEqualTo("user2");
        assertThat(result.get(2).getName()).isEqualTo("user3");
    }

    @Test
    void getAll_withoutUsers() {
        userRepository.deleteAll();

        List<UserResponseDto> result = userService.getAll();

        assertThat(result.size()).isEqualTo(0);
    }

    @AfterEach
    public void clear() {
        userRepository.deleteAll();
    }
}