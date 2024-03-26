package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemOutDto create(Long ownerId, ItemDto itemDto);

    ItemOutDto update(Long ownerId, Long itemId, Map<String, String> itemParts);

    Item getByItemIdOwnerId(Long ownerId, Long itemId);

    ItemOutDto getById(Long itemId);

    List<ItemOutDto> getByUserId(Long ownerId);

    List<ItemOutDto> searchByText(String textForSearch);
}