package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto create(@Validated @RequestBody UserRequestDto userRequestDto) {
        log.info("Запрос POST /users userDto = {}", userRequestDto);
        UserResponseDto responseUser = userService.create(userRequestDto);
        log.info("Отправлен ответ POST /users {}", responseUser);
        return responseUser;
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getUserById(@PathVariable Long userId) {
        log.info("Запрос GET /users/{}", userId);
        UserResponseDto responseUser = userService.getUserResponseById(userId);
        log.info("Отправлен ответ GET /users/{} {}", userId, responseUser);
        return responseUser;
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto update(@PathVariable Long userId, @RequestBody Map<String, String> userParts) {
        log.info("Запрос PATCH /users/{} userParts = {}", userId, userParts);
        UserResponseDto user = userService.update(userId, userParts);
        log.info("Отправлен ответ PATCH /users/{} {}", userId, user);
        return user;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long userId) {
        log.info("Запрос DELETE /users/{}", userId);
        userService.delete(userId);
        log.info("Выполнен запрос DELETE /users/{}", userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getAll() {
        log.info("Запрос GET /users");
        List<UserResponseDto> responseList = userService.getAll();
        log.info("Отправлен ответ GET /users {}", responseList);
        return responseList;
    }
}