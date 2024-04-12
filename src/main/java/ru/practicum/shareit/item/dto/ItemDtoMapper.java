package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemDtoMapper {
    private ItemDtoMapper() {
    }

    public static Item mapperToItem(CreateItemRequestDto createItemRequestDto) {
        return Item.builder()
                .name(createItemRequestDto.getName())
                .description(createItemRequestDto.getDescription())
                .available(createItemRequestDto.getAvailable())
                .build();
    }

    public static ItemOnlyResponseDto mapperToItemOutDto(Item item) {
        return ItemOnlyResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemBookingCommentsResponseDto mapperToItemBookerOutDto(Item item) {
        return ItemBookingCommentsResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }
}