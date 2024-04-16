package ru.practicum.shareit.itemRequest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.itemRequest.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface JpaItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Optional<List<ItemRequest>> findByAuthorIdOrderByCreated(Long authorId);

    Page<ItemRequest> findByAuthorIdNotOrderByCreatedDesc(Long authorId, Pageable pageable);
}
