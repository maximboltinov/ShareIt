package ru.practicum.shareit.itemRequest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class GetItemRequestResponseDto {
    private Long id;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
    private LocalDateTime created;
    private List<ShortItem> items;

    @Getter
    @Setter
    public static class ShortItem {
        private Long id;
        private String name;
        private String description;
        private Boolean available;
        private Long requestId;

        public ShortItem(Item item) {
            id = item.getId();
            name = item.getName();
            description = item.getDescription();
            available = item.getAvailable();
            requestId = item.getItemRequest().getId();
        }
    }
}