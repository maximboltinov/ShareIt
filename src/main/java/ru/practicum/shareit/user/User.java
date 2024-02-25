package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
public class User {
    @EqualsAndHashCode.Exclude
    private Long id;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String name;
}