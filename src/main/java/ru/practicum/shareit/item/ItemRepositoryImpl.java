package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> repository = new HashMap<>();
    private Long idCounter = 0L;


    @Override
    public Item create(Long ownerId, Item item) {
        item.setId(getId());
        repository.computeIfAbsent(ownerId, k -> new ArrayList<>()).add(item);

        return item;
    }

    @Override
    public Item update(Long ownerId, @Validated Item item) {
        List<Item> list = repository.get(ownerId);
        Item oldItem = getByItemIdOwnerId(ownerId, item.getId());
        int index = list.indexOf(oldItem);
        list.set(index, item);

        return item;
    }

    @Override
    public Item getById(Long itemId) {
        Optional<Item> item = repository.values().stream()
                .flatMap(Collection::stream)
                .filter(obj -> Objects.equals(obj.getId(), itemId))
                .findFirst();

        if (item.isEmpty()) {
            throw new ObjectNotFoundException("Вещь не найдена");
        } else {
            return item.get();
        }
    }

    @Override
    public List<Item> getByUserId(Long ownerId) {
        return repository.get(ownerId) == null? new ArrayList<>() : repository.get(ownerId);
    }

    @Override
    public List<Item> searchByText(String textForSearch) {
        if (repository.isEmpty()) {
            return new ArrayList<>();
        }

        String textToLower = textForSearch.toLowerCase();

        return repository.values().stream()
                .flatMap(Collection::stream)
                .filter(obj -> (obj.getName().toLowerCase().contains(textToLower)
                        || obj.getDescription().toLowerCase().contains(textToLower))
                && obj.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public Item getByItemIdOwnerId(Long ownerId, Long itemId) {
        List<Item> list = repository.get(ownerId);
        if (list == null) {
            throw new ObjectNotFoundException(String.format("Не найден Item с id %s для пользователя с id %s",
                    itemId, ownerId));
        }

        Optional<Item> item = list.stream()
                .filter(obj -> Objects.equals(obj.getId(), itemId))
                .findFirst();
        if (item.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Не найден Item с id %s для пользователя с id %s",
                    itemId, ownerId));
        } else {
            return item.get();
        }
    }

    private Long getId() {
        return ++idCounter;
    }
}
