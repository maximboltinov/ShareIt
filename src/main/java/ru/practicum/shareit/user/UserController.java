package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create (@Validated @RequestBody User user) {
        log.info("Запрос POST /users");
        User responseUser = userService.create(user);
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
    public User update (@PathVariable Long userId, @RequestBody Map<String, String> userParts) {
        log.info("Запрос PATCH /users/{}", userId);
        User user = userService.update(userId, userParts);
        log.info("Отправлен ответ PATCH /users/{} {}", userId, user);
        return user;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> getAll() {
        return userService.getAll();
    }
}