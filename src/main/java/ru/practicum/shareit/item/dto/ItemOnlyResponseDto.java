package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItemOnlyResponseDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}