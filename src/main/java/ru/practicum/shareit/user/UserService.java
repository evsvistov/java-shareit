package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    Collection<User> getUsers();

    Optional<User> getUserById(Long id);

    User createUser(User user);

    User updateUser(User user);

    boolean deleteUser(Long id);
}
