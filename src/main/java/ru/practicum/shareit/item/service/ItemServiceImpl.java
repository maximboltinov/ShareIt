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
    public ItemOutDto create(Long ownerId, ItemDto itemDto) {
        if (userService.getUserById(ownerId) == null) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }

        Item item = ItemDtoMapper.mapperToItem(itemDto);
        item.setUserId(ownerId);

        return ItemDtoMapper.mapperToItemOutDto(itemRepository.save(item));
    }

    @Override
    public ItemOutDto update(Long ownerId, Long itemId, Map<String, String> itemParts) {
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
    public ItemBookerOutDto getByItemId(Long itemId, Long userId) {
        Item item = getItemById(itemId);
        ItemBookerOutDto itemBookerOutDto = ItemDtoMapper.mapperToItemBookerOutDto(item);

        if (!Objects.equals(item.getUserId(), userId)) {
            itemBookerOutDto.setNextBooking(null);
            itemBookerOutDto.setLastBooking(null);
        } else {
            addShortBookingsToItemBookerOutDto(itemBookerOutDto,
                    bookingRepository.getShortBookingsByItemId(itemId, BookingStatus.APPROVED), LocalDateTime.now());
        }

        Optional<List<CommentOutDto>> optionalCommentOutDtoList =
                jpaCommentRepository.getCommentsOutDtoByItemId(itemId);

        if (optionalCommentOutDtoList.isEmpty()) {
            itemBookerOutDto.setComments(new ArrayList<>());
        } else {
            itemBookerOutDto.setComments(optionalCommentOutDtoList.get());
        }

        return itemBookerOutDto;
    }

    @Override
    public List<ItemBookerOutDto> getByUserId(Long ownerId) {
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

        List<ItemBookerOutDto> itemBookerOutDtoList = new ArrayList<>();

        for (Item item : itemList) {
            List<ShortBooking> shortBookingListForItem = shortBookingList.stream()
                    .filter(entity -> Objects.equals(entity.getItemId(), item.getId()))
                    .sorted(Comparator.comparing(ShortBooking::getStart))
                    .sorted(Comparator.comparing(ShortBooking::getBookerId))
                    .collect(Collectors.toList());

            itemBookerOutDtoList.add(
                    addShortBookingsToItemBookerOutDto(ItemDtoMapper.mapperToItemBookerOutDto(item),
                            shortBookingListForItem, LocalDateTime.now()));
        }

        return itemBookerOutDtoList;
    }

    @Override
    public List<ItemOutDto> searchByText(String textForSearch) {
        if (textForSearch.isBlank()) {
            return new ArrayList<>();
        }

        return itemRepository.some(textForSearch).stream()
                .map(ItemDtoMapper::mapperToItemOutDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentOutDto addComment(Long authorId, Long itemId, CommentDto text) {
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

    private ItemBookerOutDto addShortBookingsToItemBookerOutDto(ItemBookerOutDto itemBookerOutDto,
                                                                List<ShortBooking> shortBookingList,
                                                                LocalDateTime datePoint) {

        Optional<ShortBooking> lastBooking = shortBookingList.stream()
                .filter(booking -> booking.getStart().isBefore(datePoint))
                .max(ShortBooking::compareTo);


        itemBookerOutDto.setLastBooking(lastBooking.orElse(null));


        Optional<ShortBooking> nextBooking = shortBookingList.stream()
                .filter(booking -> booking.getStart().isAfter(datePoint))
                .min(ShortBooking::compareTo);

        itemBookerOutDto.setNextBooking(nextBooking.orElse(null));

        return itemBookerOutDto;
    }
}