package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ShortBooking implements Comparable<ShortBooking> {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    @JsonIgnore
    private Long itemId;

    @Override
    public int compareTo(@NonNull ShortBooking other) {
        if (this.start.isAfter(other.start)) {
            return 1;
        }
        if (this.start.isBefore(other.start)) {
            return -1;
        }
        return 0;
    }
}