package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
public class ItemDatabaseServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Autowired
    public ItemDatabaseServiceImpl(ItemRepository itemRepository, CommentRepository commentRepository,
                                   UserRepository userRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public Collection<ItemDto> getUserItems(Long userId) {
        log.info("Вывод всех вещей пользователя с id {}", userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(item -> {
                    LocalDateTime lastBooking = getLastBookingEnd(item.getId());
                    LocalDateTime nextBooking = getNextBookingStart(item.getId());
                    List<CommentDto> comments = getItemComments(item.getId());
                    return ItemMapper.toItemDto(item, comments, lastBooking, nextBooking);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItems(String text) {
        log.info("Поиск вещей по тексту: {}", text);
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.searchByText(text.toUpperCase()).stream()
                .map(item -> {
                    LocalDateTime lastBooking = getLastBookingEnd(item.getId());
                    LocalDateTime nextBooking = getNextBookingStart(item.getId());
                    List<CommentDto> comments = getItemComments(item.getId());
                    return ItemMapper.toItemDto(item, comments, lastBooking, nextBooking);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long id) {
        log.info("Поиск вещи с id: {}", id);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        LocalDateTime lastBooking = getLastBookingEnd(id);
        LocalDateTime nextBooking = getNextBookingStart(id);
        List<CommentDto> comments = getItemComments(id);

        return ItemMapper.toItemDto(item, comments, lastBooking, nextBooking);
    }

    @Override
    @Transactional
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        log.info("Добавление вещи пользователем с id: {}, вещь {}", userId, itemDto);
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (itemDto.getName() == null || itemDto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Название вещи не может быть пустым");
        }
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item createdItem = itemRepository.save(item);

        LocalDateTime lastBooking = getLastBookingEnd(createdItem.getId());
        LocalDateTime nextBooking = getNextBookingStart(createdItem.getId());
        List<CommentDto> comments = getItemComments(createdItem.getId());

        return ItemMapper.toItemDto(createdItem, comments, lastBooking, nextBooking);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Обновление вещи пользователем с id: {}, вещь {}", userId, itemDto);
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("У пользователя нет прав на изменение этой вещи");
        }

        if (itemDto.getName() != null && !itemDto.getName().trim().isEmpty()) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(existingItem);

        LocalDateTime lastBooking = getLastBookingEnd(updatedItem.getId());
        LocalDateTime nextBooking = getNextBookingStart(updatedItem.getId());
        List<CommentDto> comments = getItemComments(updatedItem.getId());

        return ItemMapper.toItemDto(updatedItem, comments, lastBooking, nextBooking);
    }

    @Override
    public List<CommentDto> getItemComments(Long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        boolean hasUserRentedItem = bookingRepository.existsByItemIdAndBookerIdAndEndIsBefore(itemId, userId, LocalDateTime.now());
        if (!hasUserRentedItem) {
            throw new BadRequestException("Пользователь не арендовал эту вещь или срок аренды еще не закончился.");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, author);
        commentRepository.save(comment);

        return CommentMapper.toCommentDto(comment);
    }

    private LocalDateTime getLastBookingEnd(Long itemId) {
        LocalDateTime now = LocalDateTime.now().withSecond(0).withNano(0);
        return bookingRepository.findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, now)
                .map(Booking::getEnd)
                .orElse(null);
    }

    private LocalDateTime getNextBookingStart(Long itemId) {
        LocalDateTime now = LocalDateTime.now();
        return bookingRepository.findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, now)
                .map(Booking::getStart)
                .orElse(null);
    }
}