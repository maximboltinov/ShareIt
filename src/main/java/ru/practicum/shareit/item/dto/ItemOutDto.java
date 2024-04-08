package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItemOutDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}