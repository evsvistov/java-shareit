package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemService {

    Collection<Item> getUserItems(Long userId);

    Collection<Item> searchItems(String text);

    Optional<Item> getItemById(Long id);

    Item createItem(Item item);

    Item updateItem(Item item);
}
