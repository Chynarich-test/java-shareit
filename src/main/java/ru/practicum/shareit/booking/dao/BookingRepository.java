package ru.practicum.shareit.booking.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
             select b
             from Booking b
             where b.booker.id = ?1
             order by b.start desc
            """)
    List<Booking> findByBookerId(long userId);

    //Получение списка всех бронирований текущего пользователя
    @Query("""
             select b
             from Booking b
             where b.booker.id = ?1
             and (b.start <= ?2 and b.end >= ?2)
             order by b.start desc
            """)
    List<Booking> findByBookerIdAndCurrent(long userId, LocalDateTime now);

    @Query("""
             select b
             from Booking b
             where b.booker.id = ?1
             and b.end < ?2
             order by b.start desc
            """)
    List<Booking> findByBookerIdAndEndPast(long userId, LocalDateTime now);

    @Query("""
             select b
             from Booking b
             where b.booker.id = ?1
             and b.start > ?2
             order by b.start desc
            """)
    List<Booking> findByBookerIdAndStartFuture(long userId, LocalDateTime now);

    @Query("""
             select b
             from Booking b
             where b.booker.id = ?1
             and b.status = ?2
             order by b.start desc
            """)
    List<Booking> findByBookerIdAndStatus(long userId, BookingStatus status);


    //Получение списка бронирований для всех вещей текущего пользователя

    @Query("""
             select b
             from Booking b
             where b.item.owner.id = ?1
             and (b.start <= ?2 and b.end >= ?2)
             order by b.start desc
            """)
    List<Booking> findBookingForItemByUserIdCurrent(long userId, LocalDateTime now);

    @Query("""
             select b
             from Booking b
             where b.item.owner.id = ?1
             and b.end < ?2
             order by b.start desc
            """)
    List<Booking> findBookingForItemByUserIdPast(long userId, LocalDateTime now);

    @Query("""
             select b
             from Booking b
             where b.item.owner.id = ?1
             and b.start > ?2
             order by b.start desc
            """)
    List<Booking> findBookingForItemByUserIdFuture(long userId, LocalDateTime now);


    @Query("""
             select b
             from Booking b
             where b.item.owner.id = ?1
             and b.status = ?2
             order by b.start desc
            """)
    List<Booking> findBookingForItemByUserIdStatus(long userId, BookingStatus status);

    @Query("""
             select b
             from Booking b
             where b.item.owner.id = ?1
             order by b.start desc
            """)
    List<Booking> findBookingForItemByUserId(long userId);

    ///

    @Query("""
                select b
                from Booking b
                where b.item.id = ?1
                  and b.end <= ?2
                order by b.end desc
            """)
    List<Booking> findLastBooking(long itemId,
                                  LocalDateTime now,
                                  Pageable pageable);

    @Query("""
                select b
                from Booking b
                where b.item.id = ?1
                  and b.start > ?2
                order by b.start asc
            """)
    List<Booking> findNextBooking(long itemId,
                                  LocalDateTime now,
                                  Pageable pageable);

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
