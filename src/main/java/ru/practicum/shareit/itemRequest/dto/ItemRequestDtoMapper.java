package ru.practicum.shareit.itemRequest.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public static GetItemRequestResponseDto toGetItemRequestResponseDto(ItemRequest itemRequest, List<Item> items) {
        GetItemRequestResponseDto getItemRequestResponseDto = GetItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();

        List<GetItemRequestResponseDto.ShortItem> shortItems = items.stream()
                .map(GetItemRequestResponseDto.ShortItem::new)
                .collect(Collectors.toList());

        getItemRequestResponseDto.setItems(shortItems);

        return getItemRequestResponseDto;
    }
}
