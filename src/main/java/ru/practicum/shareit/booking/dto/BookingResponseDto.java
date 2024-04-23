package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class BookingResponseDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private BookerId booker;
    private ItemIdName item;

    @AllArgsConstructor
    @Getter
    @Setter
    public static class BookerId {
        private Long id;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class ItemIdName {
        private Long id;
        private String name;
    }
}