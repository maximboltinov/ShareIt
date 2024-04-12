package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {
    private ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemOnlyResponseDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                      @RequestBody @Validated CreateItemRequestDto createItemRequestDto) {
        log.info("Запрос POST /items ownerId = {} itemDto = {}", ownerId, createItemRequestDto);
        ItemOnlyResponseDto responseItem = itemService.create(ownerId, createItemRequestDto);
        log.info("Отправлен ответ POST /items {}", responseItem);
        return responseItem;
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemOnlyResponseDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long itemId,
                                      @RequestBody UpdateItemRequestDto updateItem) {
        log.info("Запрос PATCH /items/{} ownerId = {} itemParts = {}", itemId, ownerId, updateItem);
        ItemOnlyResponseDto responseItemOut = itemService.update(ownerId, itemId, updateItem);
        log.info("Отправлен ответ PATCH /items/{} {}", itemId, responseItemOut);
        return responseItemOut;
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemBookingCommentsResponseDto getByItemId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @PathVariable Long itemId) {
        log.info("Запрос GET /items/{}", itemId);
        ItemBookingCommentsResponseDto responseItemOut = itemService.getByItemId(itemId, userId);
        log.info("Отправлен ответ GET /items/{} {}", itemId, responseItemOut);
        return responseItemOut;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemBookingCommentsResponseDto> getByUserId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос GET /items userId {}", ownerId);
        List<ItemBookingCommentsResponseDto> itemOutList = itemService.getByUserId(ownerId);
        log.info("Отправлен ответ GET /items userId {} {}", ownerId, itemOutList);
        return itemOutList;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemOnlyResponseDto> searchByText(@RequestParam(name = "text", required = false) String textForSearch) {
        log.info("Запрос GET /search?text={}", textForSearch);
        List<ItemOnlyResponseDto> itemOutList = itemService.searchByText(textForSearch);
        log.info("Отправлен ответ GET /search?text={} {}", textForSearch, itemOutList);
        return itemOutList;
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                         @PathVariable Long itemId,
                                         @RequestBody @Validated CommentRequestDto text) {
        log.info("Запрос POST /items/{}/comment", itemId);
        CommentResponseDto comment = itemService.addComment(authorId, itemId, text);
        log.info("Отправлен ответ POST /items/{}/comment {}", itemId, comment);
        return comment;
    }
}