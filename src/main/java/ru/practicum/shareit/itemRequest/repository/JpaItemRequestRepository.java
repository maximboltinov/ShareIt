package ru.practicum.shareit.itemRequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

public interface JpaItemRequestRepository extends JpaRepository<ItemRequest, Long> {
}
