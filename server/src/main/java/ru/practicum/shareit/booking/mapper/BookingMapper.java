package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) {
            return null;
        }
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());

        if (booking.getItem() != null) {
            ItemDto itemDto = new ItemDto();
            itemDto.setId(booking.getItem().getId());
            itemDto.setName(booking.getItem().getName());
            itemDto.setDescription(booking.getItem().getDescription());
            itemDto.setAvailable(booking.getItem().isAvailable());
            dto.setItem(itemDto);
        }

        UserDto bookerDto = UserMapper.toUserDto(booking.getBooker());
        dto.setBooker(bookerDto);

        dto.setStatus(booking.getStatus());
        return dto;
    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User booker) {
        if (bookingDto == null || item == null || booker == null) {
            return null;
        }

        Booking booking = new Booking();
        if (bookingDto.getId() != null) {
            booking.setId(bookingDto.getId());
        }
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }
}
