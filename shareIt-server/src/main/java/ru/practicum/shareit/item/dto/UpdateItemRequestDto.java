package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Positive;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UpdateItemRequestDto {
    @Positive
    private Long id;
    private String name;
    private String description;
    private Boolean available;
}
