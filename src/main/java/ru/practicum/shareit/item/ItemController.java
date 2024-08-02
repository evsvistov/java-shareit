package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

/**
 * Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid
                                              @RequestBody ItemDto itemDto) {
        ItemDto createdItem = itemService.createItem(userId, itemDto);
        return new ResponseEntity<>(createdItem, HttpStatus.OK);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody ItemDto itemDto) {
        ItemDto updatedItemDto = itemService.updateItem(userId, itemId, itemDto);
        return new ResponseEntity<>(updatedItemDto, HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        ItemDto itemDto = itemService.getItemById(itemId);
        return new ResponseEntity<>(itemDto, HttpStatus.OK);
    }

    @GetMapping
    public Collection<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }
}
