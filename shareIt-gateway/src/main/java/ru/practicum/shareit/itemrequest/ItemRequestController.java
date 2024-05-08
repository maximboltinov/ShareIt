package ru.practicum.shareit.itemrequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Positive @RequestHeader("X-Sharer-User-Id") Long authorId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Запрос POST /requests authorId = {} itemRequestDto = {}", authorId, itemRequestDto);
        ResponseEntity<Object> responseEntity = itemRequestClient.create(authorId, itemRequestDto);
        log.info("Отправлен ответ POST /requests {}", responseEntity);
        return responseEntity;
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@Positive @RequestHeader("X-Sharer-User-Id") Long authorId) {
        log.info("Запрос GET /requests authorId = {}", authorId);
        ResponseEntity<Object> responseEntity = itemRequestClient.getUserRequests(authorId);
        log.info("Отправлен ответ GET /requests {}", responseEntity);
        return responseEntity;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsAnotherUsers(
            @Positive @RequestHeader("X-Sharer-User-Id") Long userId,
            @PositiveOrZero @RequestParam(defaultValue = "0") Long from,
            @Positive @RequestParam(defaultValue = "20") Long size) {
        log.info("Запрос GET /requests/all?from={}&size{} authorId = {}", from, size, userId);
        ResponseEntity<Object> responseEntity = itemRequestClient.getAllRequestsAnotherUsers(userId, from, size);
        log.info("Отправлен ответ GET /requests/all?from={}&size{} authorId = {} {}",
                from, size, userId, responseEntity);
        return responseEntity;
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@Positive @RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Positive @PathVariable Long requestId) {
        log.info("Запрос GET /requests/{}", requestId);
        ResponseEntity<Object> responseEntity = itemRequestClient.getRequestById(requestId, userId);
        log.info("Отправлен ответ GET /requests/{} {}", requestId, responseEntity);
        return responseEntity;
    }
}