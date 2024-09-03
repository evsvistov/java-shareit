package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookingService(BookingRepository bookingRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public BookingDto createBooking(Long userId, BookItemRequestDto bookItemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(bookItemRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.isAvailable()) {
            throw new IllegalArgumentException("Вещь не доступна для бронирования");
        }

        boolean hasOverlap = bookingRepository.existsByItemIdAndBookerIdAndEndIsBefore(item.getId(), userId, bookItemRequestDto.getEnd());
        if (hasOverlap) {
            throw new IllegalArgumentException("Уже существует бронирование на данное время");
        }

        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(bookItemRequestDto.getStart());
        bookingDto.setEnd(bookItemRequestDto.getEnd());

        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        bookingDto.setItem(itemDto);

        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        booking.setStatus(Booking.BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(savedBooking);
    }

    @Transactional
    public BookingDto approveBooking(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронивание не найдено"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Пользователь не является владельцем вещи");
        }

        if (booking.getStatus() != Booking.BookingStatus.WAITING) {
            throw new IllegalStateException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? Booking.BookingStatus.APPROVED : Booking.BookingStatus.REJECTED);

        Booking updatedBooking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(updatedBooking);
    }

    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронивание не найдено"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Пользователь не имеет доступа к просмотру бронирования");
        }

        return BookingMapper.toBookingDto(booking);
    }

    public List<BookingDto> getUserBookings(Long userId, String state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Booking.BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, Booking.BookingStatus.REJECTED);
                break;
            case "ALL":
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    public List<BookingDto> getOwnerBookings(Long ownerId, String state) {
        userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        switch (state.toUpperCase()) {
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
                break;
            case "WAITING":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Booking.BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, Booking.BookingStatus.REJECTED);
                break;
            case "ALL":
            default:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
                break;
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }
}
