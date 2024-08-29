package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final InMemoryItemRepository itemRepository;
    private final UserService userService;

    @Autowired
    public ItemServiceImpl(InMemoryItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public Collection<ItemDto> getUserItems(Long userId) {
        log.info("Поиск вещей пользователя с id: {}", userId);
        return null;
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        log.info("Поиск вещей по тексту: {}", text);
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return null;
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        log.info("Поиск вещи с id: {}", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));
        return ItemMapper.toItemDto(item, null, null, null);
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.info("Добавление вещи пользователем с id: {}, вещь {}", userId, itemDto);
        User owner = userService.getUserById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto, null);
        item.setOwner(owner);
        Item createdItem = itemRepository.save(item);
        return ItemMapper.toItemDto(createdItem, null, null, null);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Обновление вещи пользователем с id: {}, вещь {}", userId, itemDto);
        Item existingItem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("У пользователя нет прав на изменение этой вещи");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);
        return ItemMapper.toItemDto(updatedItem, null, null, null);
    }

    @Override
    public List<CommentDto> getItemComments(Long itemId) {
        return List.of();
    }

    @Override
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        return null;
    }
}
