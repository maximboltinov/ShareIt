package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User create(User user);

    User getUserById(Long userId);

    User update(User user);

    void delete(Long userId);

    List<User> getAll();
}