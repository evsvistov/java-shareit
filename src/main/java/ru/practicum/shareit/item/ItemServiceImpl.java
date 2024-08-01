package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Collection<Item> getUserItems(Long userId) {
        log.info("Поиск вещей пользователя с id: {}", userId);
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchItems(String text) {
        log.info("Поиск вещей по тексту: {}", text);
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return items.values().stream()
                .filter(item -> (item.isAvailable() &&
                        (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(text.toLowerCase()))))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        log.info("Поиск вещи с id: {}", itemId);
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public Item createItem(Item item) {
        log.info("Добавление новой вещи в память: {}", item);
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        if (!items.containsKey(item.getId())) {
            log.error("Ошибка: вещь с id = {} в памяти не найдена", item.getId());
            throw new NotFoundException("Вещь с id = " + item.getId() + " в памяти не найдена");
        }
        log.info("Обновление вещи {} в памяти с ID {}: ", item, item.getId());
        items.put(item.getId(), item);
        return item;
    }

    private long getNextId() {
        long currentMaxId = items.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
