package ru.practicum.shareit.itemRequest.service;

import ru.practicum.shareit.itemRequest.dto.CreateItemRequestResponseDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;

public interface ItemRequestService {
    CreateItemRequestResponseDto create(Long authorId, ItemRequestDto itemRequestDto);
}
