package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.ShortBooking;

import java.util.List;

@Data
@Builder
public class ItemBookerOutDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ShortBooking lastBooking;
    private ShortBooking nextBooking;
    private List<CommentOutDto> comments;
}