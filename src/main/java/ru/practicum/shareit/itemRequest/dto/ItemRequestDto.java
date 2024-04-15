package ru.practicum.shareit.itemRequest.dto;

import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
public class ItemRequestDto {
    @NotBlank
    @Length(max = 512)
    private String description;
}