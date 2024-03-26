package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private UserService userService;
    private ItemRepository itemRepository;

    @Override
    public ItemOutDto create(Long ownerId, ItemDto itemDto) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        Item item = ItemDtoMapper.mapperToItem(itemDto);
        item.setUserId(ownerId);

        return ItemDtoMapper.mapperToItemOutDto(itemRepository.create(item));
    }

    @Override
    public ItemOutDto update(Long ownerId, Long itemId, Map<String, String> itemParts) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        Item item = getByItemIdOwnerId(ownerId, itemId).toBuilder().build();

        for (Map.Entry<String, String> entry : itemParts.entrySet()) {
            if (!entry.getValue().isBlank()) {
                switch (entry.getKey()) {
                    case "name":
                        item.setName(entry.getValue());
                        break;
                    case "description":
                        item.setDescription(entry.getValue());
                        break;
                    case "available":
                        item.setAvailable(Boolean.parseBoolean(entry.getValue()));
                        break;
                }
            }
        }

        return ItemDtoMapper.mapperToItemOutDto(itemRepository.update(item));
    }

    @Override
    public Item getByItemIdOwnerId(Long ownerId, Long itemId) {
        return itemRepository.getByItemIdOwnerId(ownerId, itemId);
    }

    @Override
    public ItemOutDto getById(Long itemId) {
        return ItemDtoMapper.mapperToItemOutDto(itemRepository.getById(itemId));
    }

    @Override
    public List<ItemOutDto> getByUserId(Long ownerId) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        return itemRepository.getByUserId(ownerId).stream()
                .map(ItemDtoMapper::mapperToItemOutDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemOutDto> searchByText(String textForSearch) {
        List<Item> itemList = textForSearch == null || textForSearch.isBlank()
                ? new ArrayList<>() : itemRepository.searchByText(textForSearch);

        return itemList.stream()
                .map(ItemDtoMapper::mapperToItemOutDto)
                .collect(Collectors.toList());
    }
}