package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findAllByBookerIdEquals(long userId, Pageable pageable);

    Page<Booking> findAllByBookerIdEqualsAndStatusEquals(
            long userId, BookingStatus state, Pageable pageable);

    Page<Booking> findAllByBookerIdEqualsAndStartAfter(
            long userId, LocalDateTime currentTime, Pageable pageable);

    Page<Booking> findAllByBookerIdEqualsAndEndBefore(
            long userId, LocalDateTime currentTime, Pageable pageable);

    Page<Booking> findAllByBookerIdEqualsAndStartBeforeAndEndAfter(
            long userId, LocalDateTime currentTime, LocalDateTime currentTime1, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdEqualsAndStatusEquals(
            long userId, BookingStatus state, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdEqualsAndStartAfter(
            long userId, LocalDateTime currentTime, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdEqualsAndEndBefore(
            long userId, LocalDateTime currentTime, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdEqualsAndStartBeforeAndEndAfter(
            long userId, LocalDateTime currentTime, LocalDateTime currentTime1, Pageable pageable);

    Page<Booking> findAllByItemOwnerIdEquals(long userId, Pageable pageable);

    Booking findFirstByItemIdEqualsAndStatusIsNotAndStartBeforeOrderByStartDesc(Long id, BookingStatus status, LocalDateTime now);

    Booking findFirstByItemIdEqualsAndStatusIsNotAndStartAfterOrderByStartAsc(Long id, BookingStatus status, LocalDateTime now);

    Booking findFirstByBookerIdEqualsAndItemIdEqualsAndEndBefore(long userId, long itemId, LocalDateTime now);
}
