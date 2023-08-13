package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Iterable<Booking> findAllByBookerIdEqualsOrderByStartDesc(long userId);

    List<BookingDto> findAllByItemOwnerIdEquals(long userId);
    
    Iterable<Booking> findAllByBookerIdEqualsAndStatusEqualsOrderByStartDesc(long userId, BookingStatus state);

    Iterable<Booking> findAllByBookerIdEqualsAndStartAfterOrderByStartDesc(long userId, LocalDateTime currentTime);

    Iterable<Booking> findAllByBookerIdEqualsAndEndBeforeOrderByStartDesc(long userId, LocalDateTime currentTime);

    Iterable<Booking> findAllByBookerIdEqualsAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime currentTime, LocalDateTime currentTime1);

    Iterable<Booking> findAllByItemOwnerIdEqualsAndStatusEqualsOrderByStartDesc(long userId, BookingStatus state);

    Iterable<Booking> findAllByItemOwnerIdEqualsAndStartAfterOrderByStartDesc(long userId, LocalDateTime currentTime);

    Iterable<Booking> findAllByItemOwnerIdEqualsAndEndBeforeOrderByStartDesc(long userId, LocalDateTime currentTime);

    Iterable<Booking> findAllByItemOwnerIdEqualsAndStartBeforeAndEndAfterOrderByStartDesc(long userId, LocalDateTime currentTime, LocalDateTime currentTime1);

    Iterable<Booking> findAllByItemOwnerIdEqualsOrderByStartDesc(long userId);

    Booking findFirstByItemIdEqualsAndStatusIsNotAndStartBeforeOrderByStartDesc(Long id, BookingStatus status, LocalDateTime now);

    Booking findFirstByItemIdEqualsAndStatusIsNotAndStartAfterOrderByStartAsc(Long id, BookingStatus status, LocalDateTime now);

    Booking findFirstByBookerIdEqualsAndItemIdEqualsAndEndBefore(long userId, long itemId, LocalDateTime now);
}
