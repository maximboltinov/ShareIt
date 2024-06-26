package ru.practicum.shareit.itemrequest.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.itemrequest.dto.CreateItemRequestResponseDto;
import ru.practicum.shareit.itemrequest.dto.GetItemRequestResponseDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemrequest.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.itemrequest.model.ItemRequest;
import ru.practicum.shareit.itemrequest.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final JpaUserRepository jpaUserRepository;
    private final JpaItemRequestRepository jpaItemRequestRepository;
    private final JpaItemRepository jpaItemRepository;

    @Override
    public CreateItemRequestResponseDto create(Long authorId, ItemRequestDto itemRequestDto) {
        User user = jpaUserRepository.findById(authorId)
                .orElseThrow(() ->
                        new ObjectNotFoundException(String.format("Пользователь с id = %s не найден", authorId)));

        ItemRequest itemRequest = jpaItemRequestRepository.save(
                ItemRequestDtoMapper.itemRequestDtoToItemRequest(user, itemRequestDto, LocalDateTime.now()));

        return ItemRequestDtoMapper.itemRequestToCreateItemRequestResponseDto(itemRequest);
    }

    @Override
    public List<GetItemRequestResponseDto> getUserRequests(Long authorId) {
        if (!jpaUserRepository.existsById(authorId)) {
            throw new ObjectNotFoundException("пользователь не найден");
        }

        List<ItemRequest> requests = jpaItemRequestRepository
                .findByAuthorIdOrderByCreated(authorId).orElse(List.of());

        List<Item> items = jpaItemRepository.findByItemRequest_Author_IdOrderById(authorId).orElse(List.of());

        return toListOfGetItemRequestResponseDto(requests, items);
    }

    @Override
    public List<GetItemRequestResponseDto> getAllRequestsAnotherUsers(Long userId, Long from, Long size) {
        if (!jpaUserRepository.existsById(userId)) {
            throw new ObjectNotFoundException("не найден пользователь");
        }

        Pageable pageable = PageRequest.of(Math.toIntExact(from) / Math.toIntExact(size),
                Math.toIntExact(size),
                Sort.by(Sort.Direction.DESC, "created"));

        List<ItemRequest> requests = jpaItemRequestRepository
                .findByAuthorIdNotOrderByCreatedDesc(userId, pageable).getContent();

        List<Item> items = jpaItemRepository.findByItemRequest_Author_IdNotOrderById(userId).orElse(List.of());

        return toListOfGetItemRequestResponseDto(requests, items);
    }

    @Override
    public GetItemRequestResponseDto getRequestById(Long requestId, Long userId) {
        if (!jpaUserRepository.existsById(userId)) {
            throw new ObjectNotFoundException("пользователь не найден");
        }

        ItemRequest request = jpaItemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("запрос не найден"));

        List<Item> items = jpaItemRepository.findItemByItemRequest_IdOrderById(requestId).orElse(List.of());

        return ItemRequestDtoMapper.toGetItemRequestResponseDto(request, items);
    }

    private List<GetItemRequestResponseDto> toListOfGetItemRequestResponseDto(List<ItemRequest> requests,
                                                                              List<Item> items) {
        return requests.stream()
                .map(requestEntity -> ItemRequestDtoMapper.toGetItemRequestResponseDto(requestEntity,
                        items.stream()
                                .filter(itemEntity ->
                                        Objects.equals(itemEntity.getItemRequest().getId(), requestEntity.getId()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}