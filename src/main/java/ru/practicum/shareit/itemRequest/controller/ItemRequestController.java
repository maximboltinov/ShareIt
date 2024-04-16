package ru.practicum.shareit.itemRequest.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemRequest.dto.CreateItemRequestResponseDto;
import ru.practicum.shareit.itemRequest.dto.GetItemRequestResponseDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@AllArgsConstructor
public class ItemRequestController {
    private ItemRequestService itemRequestService;

    @PostMapping
    public CreateItemRequestResponseDto create(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                               @RequestBody @Validated ItemRequestDto itemRequestDto) {
        log.info("Запрос POST /requests authorId = {} itemRequestDto = {}", authorId, itemRequestDto);
        CreateItemRequestResponseDto itemRequestResponseDto = itemRequestService.create(authorId, itemRequestDto);
        log.info("Отправлен ответ POST /requests {}", itemRequestResponseDto);
        return itemRequestResponseDto;
    }

    @GetMapping
    public List<GetItemRequestResponseDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long authorId) {
        log.info("Запрос GET /requests authorId = {}", authorId);
        List<GetItemRequestResponseDto> requests = itemRequestService.getUserRequests(authorId);
        log.info("Отправлен ответ GET /requests {}", requests);
        return requests;
    }

    @GetMapping("/all")
    public List<GetItemRequestResponseDto> getAllRequestsAnotherUsers(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                      @RequestParam (defaultValue = "0") Long from,
                                                                      @RequestParam (defaultValue = "100") Long size) {
        log.info("Запрос GET /requests/all?from={}&size{} authorId = {}", from, size, userId);
        List<GetItemRequestResponseDto> requests = itemRequestService.getAllRequestsAnotherUsers(userId, from, size);
        log.info("Отправлен ответ GET /requests/all?from={}&size{} authorId = {} {}", from, size, userId, requests);
        return requests;
    }
}