package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserResponseDto create(UserRequestDto userRequestDto);

    User getUserById(Long userId);

    UserResponseDto update(Long userId, Map<String, String> userParts);

    void delete(Long userId);

    List<UserResponseDto> getAll();

    Boolean isPresent(Long userId);

    UserResponseDto getUserResponseById(Long userId);
}