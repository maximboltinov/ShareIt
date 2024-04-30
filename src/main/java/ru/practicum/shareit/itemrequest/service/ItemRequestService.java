package ru.practicum.shareit.itemrequest.service;

import ru.practicum.shareit.itemrequest.dto.CreateItemRequestResponseDto;
import ru.practicum.shareit.itemrequest.dto.GetItemRequestResponseDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    CreateItemRequestResponseDto create(Long authorId, ItemRequestDto itemRequestDto);

    List<GetItemRequestResponseDto> getUserRequests(Long authorId);

    List<GetItemRequestResponseDto> getAllRequestsAnotherUsers(Long userId, Long from, Long size);

    GetItemRequestResponseDto getRequestById(Long requestId, Long userId);
}
