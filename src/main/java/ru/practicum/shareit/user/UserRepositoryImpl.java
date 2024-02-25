package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.exception.AvailabilityException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository{
    private final Map<Long, User> repository = new HashMap<>();
    private Long id = 0L;

    @Override
    public User create(User user) {
        if (emailIsPresent(user.getEmail())) {
            throw new AvailabilityException("Адрес электронной почты уже используется");
        }

        user.setId(getId());
        repository.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        return repository.get(userId);
    }

    @Override
    public User update(@Validated User user) {
        User userOld = repository.get(user.getId());

        if (!user.getEmail().equals(userOld.getEmail())) {
            if (emailIsPresent(user.getEmail())) {
                throw new AvailabilityException("Адрес электронной почты уже используется");
            }
        }

        repository.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        repository.remove(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(repository.values());
    }

    private Long getId() {
        return ++id;
    }

    private boolean emailIsPresent(String email) {
        return repository.values().stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }
}