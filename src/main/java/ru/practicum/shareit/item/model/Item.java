package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(toBuilder = true)
public class Item {
    @EqualsAndHashCode.Exclude
    private Long id;
    private String name;
    private String description;
    @EqualsAndHashCode.Exclude
    private Boolean available;
    private Long userId;
}