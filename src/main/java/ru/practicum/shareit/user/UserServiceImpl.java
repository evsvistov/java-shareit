package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final InMemoryUserRepository userRepository;

    @Autowired
    public UserServiceImpl(InMemoryUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Collection<UserDto> getUsers() {
        log.info("Вывод пользователей из памяти");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserDtoById(Long userId) {
        log.info("Поиск пользователя с id: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public Optional<User> getUserById(Long userId) {
        log.info("Поиск пользователя с id: {}", userId);
        return userRepository.findById(userId);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Добавление нового пользователя: {}", userDto);
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateEmailException("Пользователь с email = " + userDto.getEmail() + " уже существует");
        }
        User user = UserMapper.toUser(userDto);
        User createdUser = userRepository.save(user);
        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        log.info("Обновление пользователя с id {}", userId);
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (userDto.getEmail() != null && !existingUser.getEmail().equals(userDto.getEmail())) {
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new DuplicateEmailException("Пользователь с email = " + userDto.getEmail() + " уже существует");
            }
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public boolean deleteUser(Long userId) {
        log.info("Удаление пользователя с id {}", userId);
        if (userRepository.findById(userId).isPresent()) {
            userRepository.delete(userId);
            return true;
        }
        return false;
    }
}
