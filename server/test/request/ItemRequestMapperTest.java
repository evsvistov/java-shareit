package request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {

    @Test
    void toDto_withItems() {
        User requester = new User();
        requester.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Request description");
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        Item item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);

        List<Item> items = Collections.singletonList(item);

        ItemRequestDto dto = ItemRequestMapper.toDto(itemRequest, items);

        assertEquals(itemRequest.getId(), dto.getId());
        assertEquals(itemRequest.getDescription(), dto.getDescription());
        assertEquals(itemRequest.getRequester().getId(), dto.getRequesterId());
        assertEquals(1, dto.getItems().size());
        assertEquals(item.getId(), dto.getItems().get(0).getId());
    }

    @Test
    void toDto_withoutItems() {
        User requester = new User();
        requester.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Request description");
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());

        ItemRequestDto dto = ItemRequestMapper.toDto(itemRequest);

        assertEquals(itemRequest.getId(), dto.getId());
        assertEquals(itemRequest.getDescription(), dto.getDescription());
        assertEquals(itemRequest.getRequester().getId(), dto.getRequesterId());
    }

    @Test
    void toItemRequest() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Request description");
        dto.setCreated(LocalDateTime.now());

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(dto);

        assertEquals(dto.getId(), itemRequest.getId());
        assertEquals(dto.getDescription(), itemRequest.getDescription());
        assertEquals(dto.getCreated(), itemRequest.getCreated());
    }
}

