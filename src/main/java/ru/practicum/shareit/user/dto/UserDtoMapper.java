package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.user.model.User;

public final class UserDtoMapper {
    private UserDtoMapper() {
    }

    public static User mapperToUser(UserRequestDto userRequestDto) {
        return User.builder()
                .name(userRequestDto.getName())
                .email(userRequestDto.getEmail())
                .build();
    }

    public static UserResponseDto mapperToUserResponseDto(User user) {
        return new UserResponseDto(user.getId(), user.getEmail(), user.getName());
    }
}