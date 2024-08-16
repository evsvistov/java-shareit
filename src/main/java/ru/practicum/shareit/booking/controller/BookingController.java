package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestBody BookingDto bookingDto) {
        BookingDto createdBooking = bookingService.createBooking(userId, bookingDto);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approveBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                     @PathVariable Long bookingId,
                                                     @RequestParam boolean approved) {
        BookingDto updatedBooking = bookingService.approveBooking(ownerId, bookingId, approved);
        return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId) {
        BookingDto bookingDto = bookingService.getBooking(userId, bookingId);
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        List<BookingDto> bookings = bookingService.getUserBookings(userId, state);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getOwnerBookings(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                             @RequestParam(name = "state", defaultValue = "ALL") String state) {
        List<BookingDto> bookings = bookingService.getOwnerBookings(ownerId, state);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
}