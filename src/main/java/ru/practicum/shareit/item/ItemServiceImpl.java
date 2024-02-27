package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private UserService userService;
    private ItemRepository itemRepository;

    @Override
    public Item create(Long ownerId, ItemDto itemDto) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        return itemRepository.create(ownerId, ItemDtoMapper.mapperToItem(itemDto));
    }

    @Override
    public Item update(Long ownerId, Long itemId, Map<String, String> itemParts) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        Item item = getByItemIdOwnerId(ownerId, itemId).toBuilder().build();

        for (Map.Entry<String, String> entry: itemParts.entrySet()) {
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

        return itemRepository.update(ownerId, item);
    }

    @Override
    public Item getByItemIdOwnerId(Long ownerId, Long itemId) {
        return itemRepository.getByItemIdOwnerId(ownerId, itemId);
    }

    @Override
    public Item getById(Long itemId) {
        return itemRepository.getById(itemId);
    }

    @Override
    public List<Item> getByUserId(Long ownerId) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        return itemRepository.getByUserId(ownerId);
    }

    @Override
    public List<Item> searchByText(String textForSearch) {
        return textForSearch == null || textForSearch.isBlank()? new ArrayList<>() : itemRepository.searchByText(textForSearch);
    }
}