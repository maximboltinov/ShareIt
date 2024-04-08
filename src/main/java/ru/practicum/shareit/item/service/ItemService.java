package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

public interface ItemService {
    ItemOutDto create(Long ownerId, ItemDto itemDto);

    ItemOutDto update(Long ownerId, Long itemId, Map<String, String> itemParts);

    Item getItemById(Long itemId);

    ItemBookerOutDto getByItemId(Long itemId, Long userId);

    List<ItemBookerOutDto> getByUserId(Long ownerId);

    List<ItemOutDto> searchByText(String textForSearch);

    CommentOutDto addComment(Long authorId, Long itemId, CommentDto text);
}