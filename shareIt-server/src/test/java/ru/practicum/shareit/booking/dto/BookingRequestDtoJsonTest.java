package ru.practicum.shareit.booking.dto;

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
class BookingRequestDtoJsonTest {
    @Autowired
    JacksonTester<BookingRequestDto> tester;

    @SneakyThrows
    @Test
    public void testSerialize() {
        BookingRequestDto bookingRequestDto =
                new BookingRequestDto(1L,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(1));

        JsonContent<BookingRequestDto> result = tester.write(bookingRequestDto);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingRequestDto.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(bookingRequestDto.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
    }

    @SneakyThrows
    @Test
    public void testDeserialize() {
        String json = "{" +
                "\"itemId\":1," +
                "\"start\":\"2024-04-29T23:11:15\"," +
                "\"end\":\"2024-04-30T23:11:15\"" +
                "}";

        BookingRequestDto bookingRequestDto = tester.parseObject(json);

        assertThat(bookingRequestDto.getItemId()).isEqualTo(1L);
        assertThat(bookingRequestDto.getStart()).isEqualTo("2024-04-29T23:11:15");
        assertThat(bookingRequestDto.getEnd()).isEqualTo("2024-04-30T23:11:15");
    }
}