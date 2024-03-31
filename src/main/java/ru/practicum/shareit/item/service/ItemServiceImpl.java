package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemOutDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private UserService userService;
    private JpaItemRepository itemRepository;

    @Override
    public ItemOutDto create(Long ownerId, ItemDto itemDto) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        Item item = ItemDtoMapper.mapperToItem(itemDto);
        item.setUserId(ownerId);

        return ItemDtoMapper.mapperToItemOutDto(itemRepository.save(item));
    }

    @Override
    public ItemOutDto update(Long ownerId, Long itemId, Map<String, String> itemParts) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        Item item = getItemById(itemId).toBuilder().build();

        if (!Objects.equals(item.getUserId(), ownerId)) {
            throw new ObjectNotFoundException("Несоответствие id владельца");
        }

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

        return ItemDtoMapper.mapperToItemOutDto(itemRepository.save(item));
    }

    @Override
    public Item getItemById(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Вещь с id %s не найдена", itemId));
        }
        return item.get();
    }

    @Override
    public ItemOutDto getById(Long itemId) {
        return ItemDtoMapper.mapperToItemOutDto(getItemById(itemId));
    }

    @Override
    public List<ItemOutDto> getByUserId(Long ownerId) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        Optional<List<Item>> itemList = itemRepository.findByUserId(ownerId);

        return itemList.map(items -> items.stream()
                        .map(ItemDtoMapper::mapperToItemOutDto)
                        .collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }

    @Override
    public List<ItemOutDto> searchByText(String textForSearch) {
        if (textForSearch.isBlank()) {
            return new ArrayList<>();
        }

        List<Item> itemList = itemRepository
                .some(textForSearch);

        System.out.println(itemList);

       return itemList.stream()
               .map(ItemDtoMapper::mapperToItemOutDto)
               .collect(Collectors.toList());

//        return itemList.map(items -> items.stream()
//                .map(ItemDtoMapper::mapperToItemOutDto)
//                .collect(Collectors.toList())).orElseGet(ArrayList::new);
    }
}