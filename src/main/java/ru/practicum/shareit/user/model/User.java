package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(toBuilder = true)
public class User {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String email;
    private String name;
}