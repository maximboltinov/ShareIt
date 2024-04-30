package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BookingService bookingService;

    @SneakyThrows
    @Test
    void createCorrect() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        when(bookingService.create(anyLong(), any(BookingRequestDto.class)))
                .thenReturn(new BookingResponseDto(1L,
                        bookingRequestDto.getStart(),
                        bookingRequestDto.getEnd(),
                        BookingStatus.WAITING,
                        new BookingResponseDto.BookerId(1L),
                        new BookingResponseDto.ItemIdName(2L, "some")));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L), Long.class))
                .andExpect(jsonPath("$.start").value(bookingRequestDto.getStart().toString().substring(0, 19)))
                .andExpect(jsonPath("$.end").value(bookingRequestDto.getEnd().toString().substring(0, 19)))
                .andExpect(jsonPath("$.status").value(BookingStatus.WAITING.toString()))
                .andExpect(jsonPath("$.booker.id").value(1))
                .andExpect(jsonPath("$.item.id").value(2))
                .andExpect(jsonPath("$.item.name").value("some"));
    }

    @SneakyThrows
    @Test
    void createWithoutRequestBody() {
        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(anyLong(), any(BookingRequestDto.class));
    }

    @SneakyThrows
    @Test
    void createWithViolationOfTheConditionsForTheValueInBody() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(anyLong(), any(BookingRequestDto.class));

        bookingRequestDto.setItemId(1L);
        bookingRequestDto.setStart(null);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(anyLong(), any(BookingRequestDto.class));

        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(null);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(anyLong(), any(BookingRequestDto.class));
    }

    @SneakyThrows
    @Test
    void createWithoutXSharerUserId() {
        BookingRequestDto bookingRequestDto = new BookingRequestDto(null,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        when(bookingService.create(anyLong(), any(BookingRequestDto.class)))
                .thenReturn(new BookingResponseDto(1L,
                        bookingRequestDto.getStart(),
                        bookingRequestDto.getEnd(),
                        BookingStatus.WAITING,
                        new BookingResponseDto.BookerId(1L),
                        new BookingResponseDto.ItemIdName(2L, "some")));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(bookingService, never()).create(anyLong(), any(BookingRequestDto.class));
    }

    @SneakyThrows
    @Test
    void updateCorrect() {
        long bookingId = 1;
        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk());

        verify(bookingService).updateApprove(1L, 1L, true);
    }

    @SneakyThrows
    @Test
    void getBookingByIdCorrect() {
        long bookingId = 1;
        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(bookingService).getBookingById(1L, 1L);
    }

    @SneakyThrows
    @Test
    void getBookingsByBookerIdCorrect() {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "3"))
                .andExpect(status().isOk());

        verify(bookingService).getBookingsByBookerId(1L, BookingState.ALL, 0L, 3L);
    }

    @SneakyThrows
    @Test
    void getBookingsByOwnerIdCorrect() {
        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "3"))
                .andExpect(status().isOk());

        verify(bookingService).getBookingsByOwnerId(1L, BookingState.ALL, 0L, 3L);
    }
}