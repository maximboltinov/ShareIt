package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Long ownerId, Item item);

    Item getByItemIdOwnerId(Long ownerId, Long itemId);

    Item update(Long ownerId, Item item);

    Item getById(Long itemId);

    List<Item> getByUserId(Long ownerId);

    List<Item> searchByText(String textForSearch);
}