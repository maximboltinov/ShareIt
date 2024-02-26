package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String name;
}
