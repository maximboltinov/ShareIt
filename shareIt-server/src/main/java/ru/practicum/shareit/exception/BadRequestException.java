package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {
    private final String area;

    public BadRequestException(String areaMessage, String message) {
        super(message);
        area = areaMessage;
    }
}