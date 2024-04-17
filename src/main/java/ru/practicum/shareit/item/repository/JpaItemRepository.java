package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface JpaItemRepository extends JpaRepository<Item, Long> {
    Optional<List<Item>> findByOwnerIdOrderById(Long userId);

    @Query(value = "select it from Item it where it.available = true and " +
            "(lower(it.name) like lower(concat('%',?1,'%')) " +
            "or (lower(it.description) like lower(concat('%',?1,'%'))))")
    List<Item> some(String text);

    Optional<List<Item>> findByItemRequest_Author_IdOrderById(Long authorId);

    Optional<List<Item>> findByItemRequest_Author_IdNotOrderById(Long authorId);

    Optional<List<Item>> findItemByItemRequest_IdOrderById(Long requestId);
}