package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserResponseDto create(UserRequestDto userRequestDto);

    User getUserById(Long userId);

    UserResponseDto update(Long userId, UpdateUserRequestDto userUpdate);

    void delete(Long userId);

    List<UserResponseDto> getAll();

    Boolean isPresent(Long userId);

    UserResponseDto getUserResponseById(Long userId);
}