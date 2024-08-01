package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@Slf4j
public class UserServiceImpl implements UserService {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        log.info("Вывод пользователей из памяти");
        return users.values();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        log.info("Поиск пользователя с id: {}", id);
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User createUser(User user) {
        if (isEmailExist(user.getEmail())) {
            log.error("Ошибка: пользователь с email = {} уже существует", user.getEmail());
            throw new DuplicateEmailException("Пользователь с email = " + user.getEmail() + " уже существует");
        }
        log.info("Добавление нового пользователя в память: {}", user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!checkContainsUser(user)) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " в памяти не найден");
        }
        if (isEmailExist(user.getEmail())) {
            log.error("Ошибка: пользователь с email = {} уже существует", user.getEmail());
            throw new DuplicateEmailException("Пользователь с email = " + user.getEmail() + " уже существует");
        }
        log.info("Обновление пользователя {} в памяти с ID {}: ", user, user.getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean deleteUser(Long id) {
        log.info("Удаление пользователя с id {} из памяти", id);
        if (users.containsKey(id)) {
            users.remove(id);
            return true;
        } else {
            return false;
        }
    }

    private boolean isEmailExist(String email) {
        return users.values().stream().anyMatch(user -> Objects.equals(user.getEmail(), email));
    }

    private boolean checkContainsUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("Ошибка: пользователь с id = {} в памяти не найден", user.getId());
            return false;
        }
        return true;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
