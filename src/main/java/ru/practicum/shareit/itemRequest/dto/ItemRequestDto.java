package ru.practicum.shareit.itemRequest.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@EqualsAndHashCode
public class ItemRequestDto {
    @NotBlank
    @Length(max = 512)
    private String description;
}