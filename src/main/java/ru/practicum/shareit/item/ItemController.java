package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@AllArgsConstructor
public class ItemController {
    private ItemService itemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Item create(@RequestHeader("X-Sharer-User-Id") Long ownerId, @RequestBody @Validated ItemDto itemDto) {
        log.info("Запрос POST /items");
        Item responseItem = itemService.create(ownerId, itemDto);
        log.info("Отправлен ответ POST /items {}", responseItem);
        return responseItem;
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public Item update(@RequestHeader("X-Sharer-User-Id") Long ownerId, @PathVariable Long itemId,
                       @RequestBody Map<String, String> itemParts) {
        log.info("Запрос PATCH /items/{}", itemId);
        Item responseItem = itemService.update(ownerId, itemId, itemParts);
        log.info("Отправлен ответ PATCH /items/{} {}", itemId, responseItem);
        return responseItem;
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public Item getById(@PathVariable Long itemId) {
        log.info("Запрос GET /items/{}", itemId);
        Item responseItem = itemService.getById(itemId);
        log.info("Отправлен ответ GET /items/{} {}", itemId, responseItem);
        return responseItem;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Item> getByUserId(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Запрос GET /items userId {}", ownerId);
        List<Item> itemList = itemService.getByUserId(ownerId);
        log.info("Отправлен ответ GET /items userId {} {}", ownerId, itemList);
        return itemList;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Item> searchByText(@RequestParam(name = "text", required = false) String textForSearch) {
        log.info("Запрос GET /search?text={}", textForSearch);
        List<Item> itemList = itemService.searchByText(textForSearch);
        log.info("Отправлен ответ GET /search?text={} {}", textForSearch, itemList);
        return itemList;
    }
}