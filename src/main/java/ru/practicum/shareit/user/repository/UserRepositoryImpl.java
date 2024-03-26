package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.AvailabilityException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> repository = new HashMap<>();
    private final Set<String> emailUniqSet = new HashSet<>();
    private Long id = 0L;

    @Override
    public User create(User user) {
        if (emailUniqSet.contains(user.getEmail())) {
            throw new AvailabilityException("Адрес электронной почты уже используется");
        }

        user.setId(getId());
        repository.put(user.getId(), user);
        emailUniqSet.add(user.getEmail());
        return user;
    }

    @Override
    public User getUserById(Long userId) {
        return repository.get(userId);
    }

    @Override
    public User update(User user) {
        User userOld = repository.get(user.getId());

        if (!user.getEmail().equals(userOld.getEmail())) {
            if (emailUniqSet.contains(user.getEmail())) {
                throw new AvailabilityException("Адрес электронной почты уже используется");
            } else {
                emailUniqSet.remove(userOld.getEmail());
                emailUniqSet.add(user.getEmail());
            }
        }

        repository.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        emailUniqSet.remove(getUserById(userId).getEmail());
        repository.remove(userId);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(repository.values());
    }

    private Long getId() {
        return ++id;
    }
}