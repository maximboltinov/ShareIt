package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

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
    public User create(@Validated @RequestBody UserDto userDto) {
        log.info("Запрос POST /users userDto = {}", userDto);
        User responseUser = userService.create(userDto);
        log.info("Отправлен ответ POST /users {}", responseUser);
        return responseUser;
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable Long userId) {
        log.info("Запрос GET /users/{}", userId);
        User responseUser = userService.getUserById(userId);
        log.info("Отправлен ответ GET /users/{} {}", userId, responseUser);
        return responseUser;
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User update(@PathVariable Long userId, @RequestBody Map<String, String> userParts) {
        log.info("Запрос PATCH /users/{} userParts = {}", userId, userParts);
        User user = userService.update(userId, userParts);
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
    public List<User> getAll() {
        log.info("Запрос GET /users");
        List<User> responseList = userService.getAll();
        log.info("Отправлен ответ GET /users {}", responseList);
        return responseList;
    }
}