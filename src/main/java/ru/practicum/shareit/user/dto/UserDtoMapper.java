package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public final class UserDtoMapper {
    private UserDtoMapper() {
    }

    public static User mapperToUser(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}