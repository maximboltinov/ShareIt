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
    public Map<String, String> handlerValid(final MethodArgumentNotValidException e) {
        log.info("Завершен ошибкой", e);

        switch (e.getObjectName()) {
            case "userDto":
                return Map.of("ошибка валидации данных пользователя",
                        "указаны не все параметры или некорректный email");
            case "itemDto":
                return Map.of("ошибка валидации данных вещи", "указаны не все параметры");
            default:
                return Map.of("непредвиденная ошибка", "проверьте переданные параметры");
        }
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handlerValid(final ObjectNotFoundException e) {
        log.info("Завершен ошибкой", e);
        return Map.of("объект не найден", e.getMessage());
    }

    @ExceptionHandler(EmailValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerValid(final EmailValidException e) {
        log.info("Завершен ошибкой", e);
        return Map.of("email", e.getMessage());
    }
}