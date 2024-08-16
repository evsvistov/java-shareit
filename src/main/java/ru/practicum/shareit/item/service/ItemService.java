package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.List;

public interface ItemService {

    Collection<ItemDto> getUserItems(Long userId);

    Collection<ItemDto> searchItems(String text);

    ItemDto getItemById(Long id);

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    List<CommentDto> getItemComments(Long itemId);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
