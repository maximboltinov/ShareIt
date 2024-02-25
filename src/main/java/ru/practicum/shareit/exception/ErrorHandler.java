package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(AvailabilityException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handlerConflict(final AvailabilityException e) {
        log.info("Завершен ошибкой", e);
        return Map.of("конфликт данных", e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlerValid(final MethodArgumentNotValidException e) {
        log.info("Завершен ошибкой", e);
        return "ошибка валидации, указаны не все параметры или некорректный email";
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handlerValid(final ObjectNotFoundException e) {
        log.info("Завершен ошибкой", e);
        return Map.of("объект не найден", e.getMessage());
    }
}