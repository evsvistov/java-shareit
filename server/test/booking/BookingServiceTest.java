package booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        item = new Item();
        item.setId(1L);
        item.setOwner(user);
        item.setAvailable(true);

        booking = new Booking();
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Booking.BookingStatus.WAITING);

        // Создание объекта BookingDto из входного BookItemRequestDto
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());

        bookingDto = new BookingDto();
        bookingDto.setItem(itemDto);
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
    }

    @Test
    void createBooking_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndEndIsBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setId(1L);
            return savedBooking;
        });

        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(item.getId(), bookingDto.getStart(), bookingDto.getEnd());
        BookingDto result = bookingService.createBooking(user.getId(), bookItemRequestDto);

        assertNotNull(result);
        System.out.println("result.getItem().getId(): " + result.getItem().getId());
        System.out.println("booking.getItem().getId(): " + booking.getItem().getId());
        assertEquals(1L, result.getId());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
        assertEquals(Booking.BookingStatus.WAITING, result.getStatus());
    }


    @Test
    void createBooking_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(item.getId(), bookingDto.getStart(), bookingDto.getEnd());
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(user.getId(), bookItemRequestDto));
    }

    @Test
    void createBooking_ItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(item.getId(), bookingDto.getStart(), bookingDto.getEnd());
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(user.getId(), bookItemRequestDto));
    }

    @Test
    void createBooking_ItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(item.getId(), bookingDto.getStart(), bookingDto.getEnd());
        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(user.getId(), bookItemRequestDto));
    }

    @Test
    void createBooking_OverlappingBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndEndIsBefore(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(true);

        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto(item.getId(), bookingDto.getStart(), bookingDto.getEnd());
        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(user.getId(), bookItemRequestDto));
    }

    @Test
    void approveBooking_Success() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking savedBooking = invocation.getArgument(0);
            savedBooking.setStatus(Booking.BookingStatus.APPROVED);
            return savedBooking;
        });

        BookingDto result = bookingService.approveBooking(user.getId(), booking.getId(), true);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
        assertEquals(Booking.BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void approveBooking_BookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(user.getId(), booking.getId(), true));
    }

    @Test
    void approveBooking_UserNotOwner() {
        User otherUser = new User();
        otherUser.setId(2L);
        item.setOwner(otherUser);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class, () -> bookingService.approveBooking(user.getId(), booking.getId(), true));
    }

    @Test
    void approveBooking_BookingAlreadyProcessed() {
        booking.setStatus(Booking.BookingStatus.APPROVED);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class, () -> bookingService.approveBooking(user.getId(), booking.getId(), true));
    }

    @Test
    void getBooking_Success() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getBooking(user.getId(), booking.getId());

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(booking.getItem().getId(), result.getItem().getId());
        assertEquals(booking.getStatus(), result.getStatus());
    }

    @Test
    void getBooking_BookingNotFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(user.getId(), booking.getId()));
    }

    @Test
    void getBooking_UserNotAllowed() {
        User otherUser = new User();
        otherUser.setId(2L);
        booking.setBooker(otherUser);
        item.setOwner(otherUser);

        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThrows(AccessDeniedException.class, () -> bookingService.getBooking(user.getId(), booking.getId()));
    }

    @Test
    void getUserBookings_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByBookerIdOrderByStartDesc(anyLong())).thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getUserBookings(user.getId(), "ALL");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getUserBookings_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getUserBookings(user.getId(), "ALL"));
    }

    @Test
    void getOwnerBookings_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong())).thenReturn(Collections.singletonList(booking));

        List<BookingDto> result = bookingService.getOwnerBookings(user.getId(), "ALL");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(booking.getId(), result.get(0).getId());
    }

    @Test
    void getOwnerBookings_UserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getOwnerBookings(user.getId(), "ALL"));
    }
}
