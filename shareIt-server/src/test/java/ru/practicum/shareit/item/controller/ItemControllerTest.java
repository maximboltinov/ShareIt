package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;

    @SneakyThrows
    @Test
    void createCorrect() {
        CreateItemRequestDto createItemRequestDto = CreateItemRequestDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .requestId(null)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(createItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(itemService).create(1L, createItemRequestDto);
    }

    @SneakyThrows
    @Test
    void createWithIncorrectValuesInContent() {
        CreateItemRequestDto createItemRequestDto = CreateItemRequestDto.builder()
                .name(null)
                .description("description")
                .available(true)
                .requestId(null)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(createItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(1L, createItemRequestDto);

        createItemRequestDto.setName("name");
        createItemRequestDto.setDescription(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(createItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        createItemRequestDto.setDescription("description");
        createItemRequestDto.setAvailable(null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(createItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void updateCorrect() {
        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto(1L,
                "newName",
                "newDescription",
                false);

        String itemId = "1";

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(updateItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService).update(1L, 1L, updateItemRequestDto);

        updateItemRequestDto.setId(null);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(updateItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService).update(1L, 1L, updateItemRequestDto);

        updateItemRequestDto.setName("");

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(updateItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService).update(1L, 1L, updateItemRequestDto);

        updateItemRequestDto.setDescription("");

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(updateItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService).update(1L, 1L, updateItemRequestDto);

        updateItemRequestDto.setAvailable(null);

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(updateItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService).update(1L, 1L, updateItemRequestDto);
    }

    @SneakyThrows
    @Test
    void updateWithIncorrectValuesInContent() {
        UpdateItemRequestDto updateItemRequestDto = new UpdateItemRequestDto(-1L,
                "newName",
                "newDescription",
                false);

        String itemId = "1";

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsString(updateItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).update(1L, 1L, updateItemRequestDto);
    }

    @SneakyThrows
    @Test
    void getByItemIdCorrect() {
        String itemId = "1";

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());

        verify(itemService).getByItemId(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getByUserIdCorrect() {
        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "0")
                        .param("size", "3"))
                .andExpect(status().isOk());

        verify(itemService).getByUserId(1L, 0L, 3L);
    }

    @SneakyThrows
    @Test
    void searchByTextCorrect() {
        mockMvc.perform(get("/items/search")
                        .param("text", "some")
                        .param("from", "0")
                        .param("size", "3"))
                .andExpect(status().isOk());

        verify(itemService).searchByText("some", 0L, 3L);
    }

    @SneakyThrows
    @Test
    void addCommentCorrect() {
        String itemId = "1";

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("comment");

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsBytes(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService).addComment(1L, 1L, commentRequestDto);
    }

    @SneakyThrows
    @Test
    void addCommentWithIncorrectValuesInContent() {
        String itemId = "1";

        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("");

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", "1")
                        .content(objectMapper.writeValueAsBytes(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).addComment(1L, 1L, commentRequestDto);
    }
}