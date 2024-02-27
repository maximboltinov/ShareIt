package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    Item create(Long ownerId, ItemDto itemDto);

    Item update(Long ownerId, Long itemId, Map<String, String> itemParts);

    Item getByItemIdOwnerId(Long ownerId, Long itemId);

    Item getById(Long itemId);

    List<Item> getByUserId(Long ownerId);

    List<Item> searchByText(String textForSearch);
}