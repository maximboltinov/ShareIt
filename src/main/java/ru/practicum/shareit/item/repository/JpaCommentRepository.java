package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.Optional;

public interface JpaCommentRepository extends JpaRepository<Comment, Long> {
    @Query("select new ru.practicum.shareit.item.dto.CommentResponseDto(" +
            "comment.id, comment.text, author.name, comment.created, item.id) " +
            "from Comment comment " +
            "join comment.author author " +
            "join comment.item item " +
            "where item.id = ?1 " +
            "order by comment.id asc")
    Optional<List<CommentResponseDto>> getCommentsOutDtoByItemId(Long itemId);
}