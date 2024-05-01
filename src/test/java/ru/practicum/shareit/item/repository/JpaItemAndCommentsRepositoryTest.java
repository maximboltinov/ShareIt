package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class JpaItemAndCommentsRepositoryTest {
    @Autowired
    JpaItemRepository itemRepository;
    @Autowired
    JpaCommentRepository commentRepository;
    @Autowired
    JpaUserRepository userRepository;

    Item item1;
    Item item2;
    Item item3;

    @BeforeEach
    public void setUp() {
        User user1 = User.builder().name("user1").email("user1@email.com").build();
        user1 = userRepository.save(user1);

        item1 = Item.builder()
                .name("одна нужная вещь")
                .description("описание")
                .ownerId(user1.getId())
                .available(true)
                .itemRequest(null)
                .build();
        item1 = itemRepository.save(item1);

        item2 = Item.builder()
                .name("вещь")
                .description("комментарий к нужной вещи")
                .ownerId(user1.getId())
                .available(true)
                .itemRequest(null)
                .build();
        item2 = itemRepository.save(item2);

        item3 = Item.builder()
                .name("нечто")
                .description("описание")
                .ownerId(user1.getId())
                .available(true)
                .itemRequest(null)
                .build();
        item3 = itemRepository.save(item3);

        Item item4 = Item.builder()
                .name("нужная")
                .description("нужная")
                .ownerId(user1.getId())
                .available(false)
                .itemRequest(null)
                .build();
        item4 = itemRepository.save(item4);

        User author = User.builder().name("author").email("author@email.com").build();
        author = userRepository.save(author);

        Comment comment1 = Comment.builder()
                .author(author)
                .item(item1)
                .text("comment1 to item1")
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(comment1);

        Comment comment2 = Comment.builder()
                .author(author)
                .item(item1)
                .text("comment2 to item1")
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(comment2);

        Comment comment3 = Comment.builder()
                .author(author)
                .item(item2)
                .text("comment3 to item2")
                .created(LocalDateTime.now())
                .build();
        commentRepository.save(comment3);


    }

    @Test
    void someAllResults() {
        List<Item> result = itemRepository.some("нужн", Pageable.ofSize(5)).getContent();

        assertEquals(2, result.size());
        assertEquals("одна нужная вещь", result.get(0).getName());
        assertEquals("вещь", result.get(1).getName());
    }

    @Test
    void someOneResultShortPage() {
        List<Item> result = itemRepository.some("нужн", Pageable.ofSize(1)).getContent();

        assertEquals(1, result.size());
        assertEquals("одна нужная вещь", result.get(0).getName());
    }

    @Test
    void someNoResults() {
        List<Item> result = itemRepository.some("такогонет", Pageable.ofSize(5)).getContent();

        assertTrue(result.isEmpty());
    }

    @Test
    void someNoItems() {
        itemRepository.deleteAll();

        List<Item> result = itemRepository.some("нужн", Pageable.ofSize(5)).getContent();

        assertTrue(result.isEmpty());
    }

    @Test
    void getCommentsOutDtoByItemIdWithTwoComments() {
        Optional<List<CommentResponseDto>> optResult = commentRepository.getCommentsOutDtoByItemId(item1.getId());
        List<CommentResponseDto> result = optResult.orElse(List.of());

        assertEquals(2, result.size());
    }

    @Test
    void getCommentsOutDtoByItemIdWithOneComment() {
        Optional<List<CommentResponseDto>> optResult = commentRepository.getCommentsOutDtoByItemId(item2.getId());
        List<CommentResponseDto> result = optResult.orElse(List.of());

        assertEquals(1, result.size());
    }

    @Test
    void getCommentsOutDtoByItemIdWithoutComments() {
        Optional<List<CommentResponseDto>> optResult = commentRepository.getCommentsOutDtoByItemId(item3.getId());
        List<CommentResponseDto> result = optResult.orElse(List.of());

        assertTrue(result.isEmpty());
    }

    @AfterEach
    void clean() {
        itemRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();
    }
}