package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailValidException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User create(UserDto userDto) {
        return userRepository.create(UserDtoMapper.mapperToUser(userDto));
    }

    @Override
    public User getUserById(Long userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new ObjectNotFoundException(String.format("Нет пользователя с id = %s", userId));
        }
        return user;
    }

    @Override
    public User update(Long userId, Map<String, String> userParts) {
        User user = getUserById(userId).toBuilder().build();

        if (userParts.containsKey("name") && !userParts.get("name").isBlank()) {
            user.setName(userParts.get("name"));
        }

        if (userParts.containsKey("email") && !userParts.get("email").isBlank()) {
            emailValid(userParts.get("email"));
            user.setEmail(userParts.get("email"));
        }

        return userRepository.update(user);
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    private void emailValid(String email) {
        final Pattern pattern = Pattern.compile("\\w+([.-]?\\w+)*@\\w+([.-]?\\w+)*\\.\\w{2,4}");
        if (!pattern.matcher(email).matches()) {
            throw new EmailValidException("Некорректный email");
        }
    }
}