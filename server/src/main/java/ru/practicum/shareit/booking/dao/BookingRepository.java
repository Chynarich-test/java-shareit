package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerIdOrderByStartDesc(long userId, Pageable pageable);

    //Получение списка всех бронирований текущего пользователя
    @Query("""
             select b
             from Booking b
             where b.booker.id = ?1
             and (b.start <= ?2 and b.end >= ?2)
             order by b.start desc
            """)
    Page<Booking> findByBookerIdAndCurrent(long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStatusIsOrderByStartDesc(long userId, BookingStatus status, Pageable pageable);


    //Получение списка бронирований для всех вещей текущего пользователя

    @Query("""
             select b
             from Booking b
             where b.item.owner.id = ?1
             and (b.start <= ?2 and b.end >= ?2)
             order by b.start desc
            """)
    List<Booking> findBookingForItemByUserIdCurrent(long userId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(long userId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusIsOrderByStartDesc(long userId, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(long userId);

    ///

    List<Booking> findByItemIdInAndStatus(List<Long> itemIds, BookingStatus bookingStatus);

    @Query("""
                select (count(b) > 0)
                from Booking b
                where b.item.id = ?2
                  and b.booker.id = ?1
                  and b.status = ru.practicum.shareit.booking.BookingStatus.APPROVED
                  and b.end < ?3
            """)
    boolean userCanCreateComment(long userId, long itemId, LocalDateTime before);
}
