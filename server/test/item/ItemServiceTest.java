package item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.*;
import ru.practicum.shareit.item.repository.*;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemDatabaseServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setOwner(user);

        itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("Item Name");
        itemDto.setDescription("Item Description");
        itemDto.setAvailable(true);

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment");
        comment.setAuthor(user);
        comment.setItem(item);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Comment");
        commentDto.setAuthorName(user.getName());
        commentDto.setItemId(item.getId());
    }

    @Test
    void createItem_success() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.createItem(user.getId(), itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void createItem_userNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createItem(user.getId(), itemDto));
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void getItemById_success() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemId(item.getId())).thenReturn(singletonList(comment));

        ItemDto result = itemService.getItemById(item.getId());

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getItemById_itemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(item.getId()));
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void addComment_success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndEndIsBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertNotNull(result);
        assertEquals(commentDto.getText(), result.getText());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_userNotRentedItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndEndIsBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(BadRequestException.class, () -> itemService.addComment(user.getId(), item.getId(), commentDto));
        verify(commentRepository, never()).save(any(Comment.class));
    }
}

