package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentResponseDtoJsonTest {
    @Autowired
    JacksonTester<CommentResponseDto> tester;

    @SneakyThrows
    @Test
    public void testSerialize() {
        CommentResponseDto commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .authorName("author")
                .text("text")
                .created(LocalDateTime.now())
                .itemId(2L)
                .build();

        JsonContent<CommentResponseDto> result = tester.write(commentResponseDto);

        assertThat(result).doesNotHaveJsonPath("$.itemId");
assertThat(result).extractingJsonPathStringValue("$.created")
        .isEqualTo(commentResponseDto.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));
    }
}