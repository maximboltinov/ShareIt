package ru.practicum.shareit.itemRequest.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CreateItemRequestResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
}
