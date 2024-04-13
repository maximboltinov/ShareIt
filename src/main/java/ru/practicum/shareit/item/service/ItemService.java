package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemOnlyResponseDto create(Long ownerId, CreateItemRequestDto createItemRequestDto);

    ItemOnlyResponseDto update(Long ownerId, Long itemId, UpdateItemRequestDto updateItem);

    Item getItemById(Long itemId);

    ItemBookingCommentsResponseDto getByItemId(Long itemId, Long userId);

    List<ItemBookingCommentsResponseDto> getByUserId(Long ownerId);

    List<ItemOnlyResponseDto> searchByText(String textForSearch);

    CommentResponseDto addComment(Long authorId, Long itemId, CommentRequestDto text);
}