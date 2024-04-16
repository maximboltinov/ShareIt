package ru.practicum.shareit.itemRequest.service;

import ru.practicum.shareit.itemRequest.dto.CreateItemRequestResponseDto;
import ru.practicum.shareit.itemRequest.dto.GetItemRequestResponseDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    CreateItemRequestResponseDto create(Long authorId, ItemRequestDto itemRequestDto);

    List<GetItemRequestResponseDto> getUserRequests(Long authorId);

    List<GetItemRequestResponseDto> getAllRequestsAnotherUsers(Long userId, Long from, Long size);
}
