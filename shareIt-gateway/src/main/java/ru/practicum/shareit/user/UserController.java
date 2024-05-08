package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("Запрос POST /users userDto = {}", userRequestDto);
        ResponseEntity<Object> responseEntity = userClient.create(userRequestDto);
        log.info("Отправлен ответ POST /users {}", responseEntity);
        return responseEntity;
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserById(@PathVariable @Positive Long userId) {
        log.info("Запрос GET /users/{}", userId);
        ResponseEntity<Object> responseEntity = userClient.getUserById(userId);
        log.info("Отправлен ответ GET /users/{} {}", userId, responseEntity);
        return responseEntity;
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> update(@PathVariable @Positive Long userId, @RequestBody UpdateUserRequestDto userUpdate) {
        log.info("Запрос PATCH /users/{} userParts = {}", userId, userUpdate);
        ResponseEntity<Object> user = userClient.update(userId, userUpdate);
        log.info("Отправлен ответ PATCH /users/{} {}", userId, user);
        return user;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable @Positive Long userId) {
        log.info("Запрос DELETE /users/{}", userId);
        userClient.delete(userId);
        log.info("Выполнен запрос DELETE /users/{}", userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAll() {
        log.info("Запрос GET /users");
        ResponseEntity<Object> listResponseEntity = userClient.getAll();
        log.info("Отправлен ответ GET /users {}", listResponseEntity);
        return listResponseEntity;
    }
}
