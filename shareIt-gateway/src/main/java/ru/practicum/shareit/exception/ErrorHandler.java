package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.info("Завершен ошибкой", e);
        switch (e.getObjectName()) {
            case "userRequestDto":
                return Map.of("ошибка валидации данных пользователя",
                        "указаны не все параметры или некорректный email");
            case "createItemRequestDto":
                return Map.of("ошибка валидации данных вещи", "указаны не все параметры");
            case "bookItemRequestDto":
                return Map.of("ошибка валидации данных booking", "указаны не все параметры");
            default:
                return Map.of("непредвиденная ошибка", "проверьте переданные параметры");
        }
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerConstraintViolationException(final ConstraintViolationException e) {
        log.info("Завершен ошибкой", e);
        return Map.of("ошибка валидации", e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerIllegalArgumentException(final IllegalArgumentException e) {
        log.info("Завершен ошибкой", e);
        return Map.of("error", e.getMessage());
    }
}