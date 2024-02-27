package ru.practicum.shareit.exception;

public class AvailabilityException extends RuntimeException {
    public AvailabilityException(String massage) {
        super(massage);
    }
}