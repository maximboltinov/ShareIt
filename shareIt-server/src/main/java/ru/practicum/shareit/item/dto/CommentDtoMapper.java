package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

public final class CommentDtoMapper {
    CommentDtoMapper() {

    }

    public static CommentResponseDto commentCommentOutDtoMapper(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .itemId(comment.getItem().getId())
                .build();
    }
}