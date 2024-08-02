package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemRepository {
    private final Map<Long, Item> items = new HashMap<>();

    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(getNextId());
        }
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public Collection<Item> findByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    public Collection<Item> searchByText(String text) {
        return items.values().stream()
                .filter(item -> item.isAvailable() &&
                        (item.getName().toLowerCase().contains(text) || item.getDescription().toLowerCase().contains(text)))
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        items.remove(id);
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
