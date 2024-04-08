package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentOutDto {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
    @JsonIgnore
    private Long itemId;
}