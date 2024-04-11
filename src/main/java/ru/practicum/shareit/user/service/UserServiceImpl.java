package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.exception.EmailValidException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final JpaUserRepository userRepository;

    @Override
    public UserResponseDto create(UserRequestDto userRequestDto) {
        try {
            return UserDtoMapper
                    .mapperToUserResponseDto(userRepository.save(UserDtoMapper.mapperToUser(userRequestDto)));
        } catch (DataIntegrityViolationException e) {
            throw new AvailabilityException("Адрес электронной почты уже используется");
        }
    }

    @Override
    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Нет пользователя с id = %s", userId));
        }
        return user.get();
    }

    @Override
    public UserResponseDto update(Long userId, Map<String, String> userParts) {
        User user = getUserById(userId);

        if (userParts.containsKey("name") && !userParts.get("name").isBlank()) {
            user.setName(userParts.get("name"));
        }

        if (userParts.containsKey("email") && !userParts.get("email").isBlank()) {
            emailValid(userParts.get("email"));
            user.setEmail(userParts.get("email"));
        }

        return UserDtoMapper.mapperToUserResponseDto(userRepository.save(user));
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserResponseDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserDtoMapper::mapperToUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Boolean isPresent(Long userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public UserResponseDto getUserResponseById(Long userId) {
        return UserDtoMapper.mapperToUserResponseDto(getUserById(userId));
    }

    private void emailValid(String email) {
        final Pattern pattern = Pattern.compile("\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*\\.\\w{2,4}");
        if (!pattern.matcher(email).matches()) {
            throw new EmailValidException("Некорректный email");
        }
    }
}