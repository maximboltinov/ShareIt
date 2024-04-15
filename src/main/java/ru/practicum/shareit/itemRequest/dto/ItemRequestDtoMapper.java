package ru.practicum.shareit.itemRequest.dto;

import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public final class ItemRequestDtoMapper {
    private ItemRequestDtoMapper() {

    }

    public static ItemRequest itemRequestDtoToItemRequest(User author,
                                                          ItemRequestDto itemRequestDto,
                                                          LocalDateTime now) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setAuthor(author);
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(now);
        return itemRequest;
    }

    public static CreateItemRequestResponseDto itemRequestToCreateItemRequestResponseDto(ItemRequest itemRequest) {
        return CreateItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }
}
