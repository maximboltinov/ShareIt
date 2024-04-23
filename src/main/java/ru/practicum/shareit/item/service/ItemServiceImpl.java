package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.ShortBooking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaCommentRepository;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private UserService userService;
    private JpaBookingRepository bookingRepository;
    private JpaItemRepository itemRepository;
    private JpaCommentRepository jpaCommentRepository;
    private JpaItemRequestRepository jpaItemRequestRepository;

    @Override
    public ItemOnlyResponseDto create(Long ownerId, CreateItemRequestDto createItemRequestDto) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        ItemRequest itemRequest = null;

        if (createItemRequestDto.getRequestId() != null) {
            Optional<ItemRequest> optionalItemRequest = jpaItemRequestRepository.findById(
                    createItemRequestDto.getRequestId());
            if (optionalItemRequest.isPresent()) {
                itemRequest = optionalItemRequest.get();
            } else {
                throw new ObjectNotFoundException("запрос на добавление вещи не найден");
            }
        }

        Item item = ItemDtoMapper.mapperToItem(createItemRequestDto, ownerId, itemRequest);

        return ItemDtoMapper.mapperToItemOutDto(itemRepository.save(item));
    }

    @Override
    public ItemOnlyResponseDto update(Long ownerId, Long itemId, UpdateItemRequestDto updateItem) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        if (updateItem == null) {
            throw new BadRequestException("ItemService update", "updateItem не может быть null");
        }

        Item item = getItemById(itemId);

        if (!Objects.equals(item.getOwnerId(), ownerId)) {
            throw new ObjectNotFoundException("Несоответствие id владельца");
        }

        if (updateItem.getName() != null && !updateItem.getName().isBlank()) {
            item.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null && !updateItem.getDescription().isBlank()) {
            item.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        }

        return ItemDtoMapper.mapperToItemOutDto(itemRepository.save(item));
    }

    @Override
    public Item getItemById(Long itemId) {
        if(itemId == null) {
            throw new BadRequestException("ItemService getItemById", "itemId не может быть null");
        }

        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Вещь с id %s не найдена", itemId));
        }
        return item.get();
    }

    @Override
    public ItemBookingCommentsResponseDto getByItemId(Long itemId, Long userId) {
        if (userId == null) {
            throw new BadRequestException("ItemService getByItemId","userId не может быть null");
        }

        if (!userService.isPresent(userId)) {
            throw new ObjectNotFoundException("пользователь не найден");
        }

        Item item = getItemById(itemId);
        ItemBookingCommentsResponseDto itemBookingCommentsResponseDto = ItemDtoMapper.mapperToItemBookerOutDto(item);

        if (!Objects.equals(item.getOwnerId(), userId)) {
            itemBookingCommentsResponseDto.setNextBooking(null);
            itemBookingCommentsResponseDto.setLastBooking(null);
        } else {
            addShortBookingsToItemBookerOutDto(itemBookingCommentsResponseDto,
                    bookingRepository.getShortBookingsByItemId(itemId, BookingStatus.APPROVED), LocalDateTime.now());
        }

        Optional<List<CommentResponseDto>> optionalCommentOutDtoList =
                jpaCommentRepository.getCommentsOutDtoByItemId(itemId);

        if (optionalCommentOutDtoList.isEmpty()) {
            itemBookingCommentsResponseDto.setComments(new ArrayList<>());
        } else {
            itemBookingCommentsResponseDto.setComments(optionalCommentOutDtoList.get());
        }

        return itemBookingCommentsResponseDto;
    }

    @Override
    public List<ItemBookingCommentsResponseDto> getByUserId(Long ownerId, Long from, Long size) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        if (from < 0 || size <= 0) {
            throw new BadRequestException("ItemService getByUserId", "некорректные параметры страницы");
        }

        Pageable pageable = PageRequest.of(Math.toIntExact(from) / Math.toIntExact(size),
                Math.toIntExact(size),
                Sort.by(Sort.Direction.ASC, "id"));

        List<Item> items = itemRepository.findItemByOwnerId(ownerId, pageable).getContent();

        List<ShortBooking> shortBookingList =
                bookingRepository.getShortBookingsByItemsOwnerId(ownerId, BookingStatus.APPROVED);

        List<ItemBookingCommentsResponseDto> itemBookingCommentsResponseDtoList = new ArrayList<>();

        for (Item item : items) {
            List<ShortBooking> shortBookingListForItem = shortBookingList.stream()
                    .filter(entity -> Objects.equals(entity.getItemId(), item.getId()))
                    .sorted(Comparator.comparing(ShortBooking::getStart))
                    .sorted(Comparator.comparing(ShortBooking::getBookerId))
                    .collect(Collectors.toList());

            itemBookingCommentsResponseDtoList.add(
                    addShortBookingsToItemBookerOutDto(ItemDtoMapper.mapperToItemBookerOutDto(item),
                            shortBookingListForItem, LocalDateTime.now()));
        }

        return itemBookingCommentsResponseDtoList;
    }

    @Override
    public List<ItemOnlyResponseDto> searchByText(String textForSearch, Long from, Long size) {
        if (textForSearch.isBlank()) {
            return new ArrayList<>();
        }

        if (from < 0 || size <= 0) {
            throw new BadRequestException("ItemService searchByText", "некорректные параметры страницы");
        }

        Pageable pageable = PageRequest.of(Math.toIntExact(from) / Math.toIntExact(size),
                Math.toIntExact(size),
                Sort.by(Sort.Direction.ASC, "id"));

        return itemRepository.some(textForSearch, pageable).stream()
                .map(ItemDtoMapper::mapperToItemOutDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto addComment(Long authorId, Long itemId, CommentRequestDto text) {
        if (!userService.isPresent(authorId)) {
            throw new BadRequestException("addComment", "Пользователь не существует");
        }

        if (!itemRepository.existsById(itemId)) {
            throw new BadRequestException("addComment", "Вещь не существует");
        }

        if (bookingRepository.countApprovedBookingsForUserAndItemAnEarlyEndDate(
                        authorId, itemId, LocalDateTime.now())
                .isEmpty()) {
            throw new BadRequestException("addComment",
                    "Пользователь еще не арендовал эту вещь или аренда еще не закончилась");
        }

        Comment comment = Comment.builder()
                .author(userService.getUserById(authorId))
                .item(itemRepository.findById(itemId).get())
                .text(text.getText())
                .created(LocalDateTime.now())
                .build();

        return CommentDtoMapper.commentCommentOutDtoMapper(jpaCommentRepository.save(comment));
    }

    private ItemBookingCommentsResponseDto addShortBookingsToItemBookerOutDto(
            ItemBookingCommentsResponseDto itemBookingCommentsResponseDto,
            List<ShortBooking> shortBookingList,
            LocalDateTime datePoint) {

        Optional<ShortBooking> lastBooking = shortBookingList.stream()
                .filter(booking -> booking.getStart().isBefore(datePoint))
                .max(ShortBooking::compareTo);


        itemBookingCommentsResponseDto.setLastBooking(lastBooking.orElse(null));


        Optional<ShortBooking> nextBooking = shortBookingList.stream()
                .filter(booking -> booking.getStart().isAfter(datePoint))
                .min(ShortBooking::compareTo);

        itemBookingCommentsResponseDto.setNextBooking(nextBooking.orElse(null));

        return itemBookingCommentsResponseDto;
    }
}