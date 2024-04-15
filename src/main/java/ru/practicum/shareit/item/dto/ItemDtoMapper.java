package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

public class ItemDtoMapper {
    private ItemDtoMapper() {
    }

    public static Item mapperToItem(CreateItemRequestDto createItemRequestDto, Long ownerId, ItemRequest itemRequest) {
        return Item.builder()
                .name(createItemRequestDto.getName())
                .description(createItemRequestDto.getDescription())
                .available(createItemRequestDto.getAvailable())
                .ownerId(ownerId)
                .itemRequest(itemRequest)
                .build();
    }

    public static ItemOnlyResponseDto mapperToItemOutDto(Item item) {
        return ItemOnlyResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getItemRequest() == null ? null : item.getItemRequest().getId())
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