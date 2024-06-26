package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(Long ownerId, CreateItemRequestDto createItemRequestDto) {
        return post("", ownerId, createItemRequestDto);
    }

    public ResponseEntity<Object> update(Long ownerId, Long itemId, UpdateItemRequestDto updateItemRequestDto) {
        return patch("/" + itemId, ownerId, updateItemRequestDto);
    }

    public ResponseEntity<Object> getByItemId(Long itemId, Long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getByUserId(Long ownerId, Long from, Long size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );

        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> searchByText(String textForSearch, Long from, Long size) {
        Map<String, Object> parameters = Map.of(
                "text", textForSearch,
                "from", from,
                "size", size
        );

        return get("/search?text={text}&from={from}&size={size}", parameters);
    }

    public ResponseEntity<Object> addComment(Long authorId, Long itemId, CommentRequestDto text) {
        return post("/" + itemId + "/comment", authorId, text);
    }
}
