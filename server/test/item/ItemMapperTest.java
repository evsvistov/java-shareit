package item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemMapperTest {

    @Test
    void toItem() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        ItemDto itemDto = new ItemDto(
                1L,
                "Item Name",
                "Item Description",
                true,
                1L,
                null,
                null,
                Collections.emptyList()
        );

        Item item = ItemMapper.toItem(itemDto, itemRequest);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.isAvailable());
        assertEquals(itemRequest, item.getRequest());
    }

    @Test
    void toItemDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setRequest(itemRequest);

        LocalDateTime now = LocalDateTime.now();
        List<CommentDto> comments = Collections.singletonList(new CommentDto());

        ItemDto itemDto = ItemMapper.toItemDto(item, comments, now, now.plusDays(1));

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.isAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
        assertEquals(comments, itemDto.getComments());
        assertEquals(now, itemDto.getLastBooking());
        assertEquals(now.plusDays(1), itemDto.getNextBooking());
    }
}

