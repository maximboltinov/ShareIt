package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.EmailValidException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    JpaUserRepository userRepository;

    UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createErrorDoublingEmailAddress() {
        when(userRepository.save(User.builder().id(null).name("first").email("first@email.com").build()))
                .thenThrow(new AvailabilityException("Адрес электронной почты уже используется"));

        final AvailabilityException exception =
                assertThrows(AvailabilityException.class,
                        () -> userService.create(new UserRequestDto("first@email.com", "first")));

        assertEquals("Адрес электронной почты уже используется", exception.getMessage());
    }

    @Test
    void createCorrectParameters() {
        when(userRepository.save(User.builder().id(null).name("first").email("first@email.com").build()))
                .thenReturn(User.builder().id(1L).name("first").email("first@email.com").build());

        UserResponseDto user = userService.create(new UserRequestDto("first@email.com", "first"));

        verify(userRepository, times(1))
                .save(User.builder().id(null).name("first").email("first@email.com").build());

        assertEquals(user.getId(), 1L);
        assertEquals(user.getName(), "first");
        assertEquals(user.getEmail(), "first@email.com");
    }

    @Test
    void getUserByIdNoUser() {
        when(userRepository.findById(2L))
                .thenReturn(Optional.empty());

        final ObjectNotFoundException exception =
                assertThrows(ObjectNotFoundException.class,
                        () -> userService.getUserById(2L));

        assertEquals("Нет пользователя с id = 2", exception.getMessage());
    }

    @Test
    void getUserByIdUserPresent() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(User.builder().id(1L).name("first").email("first@email.com").build()));

        User user = userService.getUserById(1L);

        assertEquals(user.getId(), 1L);
        assertEquals(user.getName(), "first");
        assertEquals(user.getEmail(), "first@email.com");
    }

    @Test
    void updateWithNoUserById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        final ObjectNotFoundException exception =
                assertThrows(ObjectNotFoundException.class,
                        () -> userService
                                .update(1L, new UpdateUserRequestDto("email@mail.com", "user")));

        assertEquals("Нет пользователя с id = 1", exception.getMessage());
    }

    @Test
    void updateWithIncorrectEmail() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(User.builder().id(1L).name("first").email("first@email.com").build()));

        final EmailValidException exception =
                assertThrows(EmailValidException.class,
                        () -> userService
                                .update(1L, new UpdateUserRequestDto("email@@mail.com", "user")));

        assertEquals("Некорректный email", exception.getMessage());
    }

    @Test
    void updateCorrect() {
        User original = User.builder().id(1L).name("first").email("first@email.com").build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(original));

        when(userRepository.save(User.builder().id(1L).name("newfirst").email("newfirst@email.com").build()))
                .thenReturn(User.builder().id(1L).name("newfirst").email("newfirst@email.com").build());

        UserResponseDto user = userService.update(1L,
                new UpdateUserRequestDto("newfirst@email.com", "newfirst"));

        assertEquals(user.getId(), 1L);
        assertEquals(user.getName(), "newfirst");
        assertEquals(user.getEmail(), "newfirst@email.com");

        verify(userRepository, times(1))
                .save(User.builder().id(1L).name("newfirst").email("newfirst@email.com").build());

        verify(userRepository, times(1))
                .findById(1L);
    }

    @Test
    void deleteNoUserById() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> userService.delete(1L));

        assertEquals("пользователь не найден", exception.getMessage());
    }

    @Test
    void deleteCorrect() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAll() {
        User user1 = User.builder().id(1L).email("mail1@mail.com").name("name1").build();
        User user2 = User.builder().id(2L).email("mail2@mail.com").name("name2").build();

        when(userRepository.findAll())
                .thenReturn(List.of(user1,user2));

        List<UserResponseDto> users = userService.getAll();

        verify(userRepository, times(1))
                .findAll();

        assertEquals(2, users.size());
        assertEquals(users.get(0), new UserResponseDto(1L, "mail1@mail.com", "name1"));
        assertEquals(users.get(1), new UserResponseDto(2L, "mail2@mail.com", "name2"));
    }

    @Test
    void getAllNoUsers() {
        when(userRepository.findAll())
                .thenReturn(List.of());

        List<UserResponseDto> users = userService.getAll();

        verify(userRepository, times(1))
                .findAll();

        assertEquals(0, users.size());
    }
}