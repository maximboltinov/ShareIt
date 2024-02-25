package ru.practicum.shareit.user;

import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.List;
import java.util.Map;

public interface UserService {
    User create(User user);

    User getUserById(Long userId);

    User update(Long userId, Map<String, String> userParts);

    void delete(Long userId);

    List<User> getAll();
}
