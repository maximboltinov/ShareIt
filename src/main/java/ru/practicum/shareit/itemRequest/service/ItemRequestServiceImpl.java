package ru.practicum.shareit.itemRequest.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.itemRequest.dto.CreateItemRequestResponseDto;
import ru.practicum.shareit.itemRequest.dto.GetItemRequestResponseDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private JpaUserRepository jpaUserRepository;
    private JpaItemRequestRepository jpaItemRequestRepository;
    private JpaItemRepository jpaItemRepository;

    @Override
    public CreateItemRequestResponseDto create(Long authorId, ItemRequestDto itemRequestDto) {
        Optional<User> userOptional = jpaUserRepository.findById(authorId);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            throw new ObjectNotFoundException(String.format("Пользователь с id = %s не найден", authorId));
        }

        ItemRequest itemRequest = jpaItemRequestRepository.save(
                ItemRequestDtoMapper.itemRequestDtoToItemRequest(user, itemRequestDto, LocalDateTime.now()));

        return ItemRequestDtoMapper.itemRequestToCreateItemRequestResponseDto(itemRequest);
    }

    @Override
    public List<GetItemRequestResponseDto> getUserRequests(Long authorId) {
        if (!jpaUserRepository.existsById(authorId)) {
            throw new ObjectNotFoundException("пользователь не найден");
        }

        List<ItemRequest> itemRequestList = jpaItemRequestRepository
                .findByAuthorIdOrderByCreated(authorId).orElse(List.of());

        List<Item> itemList = jpaItemRepository.findByItemRequest_Author_IdOrderById(authorId).orElse(List.of());

        List<GetItemRequestResponseDto> requestsWithItems = itemRequestList.stream()
                .map(requestEntity -> ItemRequestDtoMapper.toGetItemRequestResponseDto(requestEntity,
                        itemList.stream()
                                .filter(itemEntity ->
                                        Objects.equals(itemEntity.getItemRequest().getId(), requestEntity.getId()))
                                .collect(Collectors.toList())))
                .collect(Collectors.toList());


        return requestsWithItems;
    }
}
