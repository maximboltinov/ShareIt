package ru.practicum.shareit.itemRequest.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.itemRequest.dto.CreateItemRequestResponseDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDto;
import ru.practicum.shareit.itemRequest.dto.ItemRequestDtoMapper;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private JpaUserRepository jpaUserRepository;
    private JpaItemRequestRepository jpaItemRequestRepository;

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
}
