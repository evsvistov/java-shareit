package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Проверка существования брнирования для вещи
    boolean existsByItemIdAndBookerIdAndEndIsBefore(Long itemId, Long bookerId, LocalDateTime end);

    // Все бронирования для пользователя
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    // Все бронирования по ID владельца вещи
    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    // Текущие бронирование
    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId, LocalDateTime start, LocalDateTime end);

    // Завершенные бронирования
    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    // Будущие бронирования
    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    // Бронирования по статусу WAITING, APPROVED, REJECTED
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Booking.BookingStatus status);

    // Текущие бронирования для вещей по владельцу
    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId, LocalDateTime start, LocalDateTime end);

    // Зевершенные бронирования по владельцу
    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    // Будущие бронирования по владельцу
    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    // Бронирования вещей по статусу WAITING, APPROVED, REJECTED
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, Booking.BookingStatus status);

    // Последнее бронирования перед текущей датой
    Optional<Booking> findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    // Следующего бронирования после текущей даты
    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime start);
}
