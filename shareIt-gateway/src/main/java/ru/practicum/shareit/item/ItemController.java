package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Positive @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                         @Valid @RequestBody CreateItemRequestDto createItemRequestDto) {
        log.info("Запрос POST /items ownerId = {} itemDto = {}", ownerId, createItemRequestDto);
        ResponseEntity<Object> responseEntity = itemClient.create(ownerId, createItemRequestDto);
        log.info("Отправлен ответ POST /items {}", responseEntity);
        return responseEntity;
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@Positive @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                         @Positive @PathVariable Long itemId,
                                         @Valid @RequestBody UpdateItemRequestDto updateItemRequestDto) {
        log.info("Запрос PATCH /items/{} ownerId = {} itemParts = {}", itemId, ownerId, updateItemRequestDto);
        ResponseEntity<Object> responseEntity = itemClient.update(ownerId, itemId, updateItemRequestDto);
        log.info("Отправлен ответ PATCH /items/{} {}", itemId, responseEntity);
        return responseEntity;
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getByItemId(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Positive @PathVariable Long itemId) {
        log.info("Запрос GET /items/{}", itemId);
        ResponseEntity<Object> responseEntity = itemClient.getByItemId(itemId, userId);
        log.info("Отправлен ответ GET /items/{} {}", itemId, responseEntity);
        return responseEntity;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getByUserId(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                              @RequestParam(defaultValue = "0") @PositiveOrZero Long from,
                                              @RequestParam(defaultValue = "20") @Positive Long size) {
        log.info("Запрос GET /items userId {}", ownerId);
        ResponseEntity<Object> responseEntity = itemClient.getByUserId(ownerId, from, size);
        log.info("Отправлен ответ GET /items userId {} {}", ownerId, responseEntity);
        return responseEntity;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> searchByText(@RequestParam(name = "text") String textForSearch,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Long from,
                                               @Positive @RequestParam(defaultValue = "20") Long size) {
        log.info("gateway Запрос GET /search?text={}", textForSearch);
        ResponseEntity<Object> responseEntity = itemClient.searchByText(textForSearch, from, size);
        log.info("gateway Отправлен ответ GET /search?text={} {}", textForSearch, responseEntity);
        return responseEntity;
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long authorId,
                                             @Positive @PathVariable Long itemId,
                                             @Valid @RequestBody CommentRequestDto text) {
        log.info("Запрос POST /items/{}/comment", itemId);
        ResponseEntity<Object> responseEntity = itemClient.addComment(authorId, itemId, text);
        log.info("Отправлен ответ POST /items/{}/comment {}", itemId, responseEntity);
        return responseEntity;
    }
}