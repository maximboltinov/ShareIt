package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
public class Item {
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @EqualsAndHashCode.Exclude
    @NotNull
    private Boolean available;
}