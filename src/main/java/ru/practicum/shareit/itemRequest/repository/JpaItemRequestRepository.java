package ru.practicum.shareit.itemRequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface JpaItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Optional<List<ItemRequest>> findByAuthorIdOrderByCreated(Long authorId);
}
