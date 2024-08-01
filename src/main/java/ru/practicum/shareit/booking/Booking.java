package ru.practicum.shareit.booking;

import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private long id;
    private LocalDate start;
    private LocalDate end;
    private long item;
    private long booker;
    private BookingStatus status;

    public enum BookingStatus {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }
}
