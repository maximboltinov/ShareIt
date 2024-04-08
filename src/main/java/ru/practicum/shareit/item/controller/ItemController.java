package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {
    private ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemOutDto create(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestBody @Validated ItemDto itemDto) {
        log.info("Запрос POST /items ownerId = {} itemDto = {}", ownerId, itemDto);
        ItemOutDto responseItem = itemService.create(ownerId, itemDto);
        log.info("Отправлен ответ POST /items {}", responseItem);
        return responseItem;
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemOutDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long itemId,
                             @RequestBody Map<String, String> itemParts) {
        log.info("Запрос PATCH /items/{} ownerId = {} itemParts = {}", itemId, ownerId, itemParts);
        ItemOutDto responseItemOut = itemService.update(ownerId, itemId, itemParts);
        log.info("Отправлен ответ PATCH /items/{} {}", itemId, responseItemOut);
        return responseItemOut;
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemBookerOutDto getByItemId(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Запрос GET /items/{}", itemId);
        ItemBookerOutDto responseItemOut = itemService.getByItemId(itemId, userId);
        log.info("Отправлен ответ GET /items/{} {}", itemId, responseItemOut);
        return responseItemOut;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemBookerOutDto> getByUserId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос GET /items userId {}", ownerId);
        List<ItemBookerOutDto> itemOutList = itemService.getByUserId(ownerId);
        log.info("Отправлен ответ GET /items userId {} {}", ownerId, itemOutList);
        return itemOutList;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemOutDto> searchByText(@RequestParam(name = "text", required = false) String textForSearch) {
        log.info("Запрос GET /search?text={}", textForSearch);
        List<ItemOutDto> itemOutList = itemService.searchByText(textForSearch);
        log.info("Отправлен ответ GET /search?text={} {}", textForSearch, itemOutList);
        return itemOutList;
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentOutDto addComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                    @PathVariable Long itemId,
                                    @RequestBody @Validated CommentDto text) {
        System.out.println("authorId = " + authorId + ", itemId = " + itemId + ", text = " + text + ", text = " + text.getText());

        log.info("Запрос POST /items/{}/comment", itemId);
        CommentOutDto comment = itemService.addComment(authorId, itemId, text);
        log.info("Отправлен ответ POST /items/{}/comment {}", itemId, comment);
        return comment;
    }
}