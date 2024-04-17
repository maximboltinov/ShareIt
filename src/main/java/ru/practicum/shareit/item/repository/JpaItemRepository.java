package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface JpaItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findItemByOwnerId(Long userId, Pageable pageable);

    @Query(value = "select it from Item it where it.available = true and " +
            "(lower(it.name) like lower(concat('%',?1,'%')) " +
            "or (lower(it.description) like lower(concat('%',?1,'%'))))")
    Page<Item> some(String text, Pageable pageable);

    Optional<List<Item>> findByItemRequest_Author_IdOrderById(Long authorId);

    Optional<List<Item>> findByItemRequest_Author_IdNotOrderById(Long authorId);

    Optional<List<Item>> findItemByItemRequest_IdOrderById(Long requestId);
}