package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    User create(UserDto userDto);

    User getUserById(Long userId);

    User update(Long userId, Map<String, String> userParts);

    void delete(Long userId);

    List<User> getAll();
}