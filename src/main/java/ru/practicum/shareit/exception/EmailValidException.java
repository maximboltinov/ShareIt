package ru.practicum.shareit.exception;

public class EmailValidException extends RuntimeException {
    public EmailValidException(String message) {
        super(message);
    }
}
