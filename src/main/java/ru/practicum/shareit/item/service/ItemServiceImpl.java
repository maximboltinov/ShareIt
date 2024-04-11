package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
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

    @Override
    public ItemOnlyResponseDto create(Long ownerId, ItemRequestDto itemRequestDto) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        Item item = ItemDtoMapper.mapperToItem(itemRequestDto);
        item.setUserId(ownerId);

        return ItemDtoMapper.mapperToItemOutDto(itemRepository.save(item));
    }

    @Override
    public ItemOnlyResponseDto update(Long ownerId, Long itemId, Map<String, String> itemParts) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        Item item = getItemById(itemId).toBuilder().build();

        if (!Objects.equals(item.getUserId(), ownerId)) {
            throw new ObjectNotFoundException("Несоответствие id владельца");
        }

        for (Map.Entry<String, String> entry : itemParts.entrySet()) {
            if (!entry.getValue().isBlank()) {
                switch (entry.getKey()) {
                    case "name":
                        item.setName(entry.getValue());
                        break;
                    case "description":
                        item.setDescription(entry.getValue());
                        break;
                    case "available":
                        item.setAvailable(Boolean.parseBoolean(entry.getValue()));
                        break;
                }
            }
        }

        return ItemDtoMapper.mapperToItemOutDto(itemRepository.save(item));
    }

    @Override
    public Item getItemById(Long itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ObjectNotFoundException(String.format("Вещь с id %s не найдена", itemId));
        }
        return item.get();
    }

    @Override
    public ItemBookingCommentsResponseDto getByItemId(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        ItemBookingCommentsResponseDto itemBookingCommentsResponseDto = ItemDtoMapper.mapperToItemBookerOutDto(item);

        if (!Objects.equals(item.getUserId(), userId)) {
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
    public List<ItemBookingCommentsResponseDto> getByUserId(Long ownerId) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        Optional<List<Item>> optionalItemList = itemRepository.findByUserIdOrderById(ownerId);

        if (optionalItemList.isEmpty()) {
            return new ArrayList<>();
        }

        List<Item> itemList = optionalItemList.get();
        List<ShortBooking> shortBookingList =
                bookingRepository.getShortBookingsByItemsOwnerId(ownerId, BookingStatus.APPROVED);

        List<ItemBookingCommentsResponseDto> itemBookingCommentsResponseDtoList = new ArrayList<>();

        for (Item item : itemList) {
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
    public List<ItemOnlyResponseDto> searchByText(String textForSearch) {
        if (textForSearch.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.some(textForSearch).stream()
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

    private ItemBookingCommentsResponseDto addShortBookingsToItemBookerOutDto(ItemBookingCommentsResponseDto itemBookingCommentsResponseDto,
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