package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRequestType;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public BookingDto create(BookingCreateDto bookingCreateDto, long userId) {
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        if (!item.isAvailable()) {
            throw new ValidationException("Предмет недоступен для бронирования");
        }

        Booking booking = BookingMapper.fromCreateDto(bookingCreateDto, item, user);

        booking.setStatus(BookingStatus.WAITING);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    public BookingDto confirmBooking(long bookingId, boolean status, long userId) {
        Booking existingBook = privateGet(bookingId);

        if (!existingBook.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException("Только владелец вещи может подтвердить бронирование");
        }

        existingBook.setStatus(status ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingDto(bookingRepository.save(existingBook));
    }

    public BookingDto getBooking(long bookingId, long userId) {
        Booking existingBook = privateGet(bookingId);

        if (!existingBook.getBooker().getId().equals(userId)
                && !existingBook.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является создателем брони или владельцем вещи");
        }

        return BookingMapper.toBookingDto(existingBook);
    }

    public List<BookingDto> getAllBooking(long userId, BookingRequestType status) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Booking> booking;

        switch (status) {
            case ALL -> booking = bookingRepository.findByBookerId(userId);
            case CURRENT -> booking = bookingRepository.findByBookerIdAndCurrent(userId, LocalDateTime.now());
            case PAST -> booking = bookingRepository.findByBookerIdAndEndPast(userId, LocalDateTime.now());
            case FUTURE -> booking = bookingRepository.findByBookerIdAndStartFuture(userId, LocalDateTime.now());
            case WAITING, REJECTED ->
                    booking = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.valueOf(status.name()));
            default -> throw new NotFoundException("Неверные параметры запроса");
        }

        return booking.stream().map(BookingMapper::toBookingDto).toList();
    }

    public List<BookingDto> getAllItemsBookings(long userId, BookingRequestType status) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Booking> booking;

        switch (status) {
            case ALL -> booking = bookingRepository.findBookingForItemByUserId(userId);
            case CURRENT -> booking = bookingRepository.findBookingForItemByUserIdCurrent(userId, LocalDateTime.now());
            case PAST -> booking = bookingRepository.findBookingForItemByUserIdPast(userId, LocalDateTime.now());
            case FUTURE -> booking = bookingRepository.findBookingForItemByUserIdFuture(userId, LocalDateTime.now());
            case WAITING, REJECTED ->
                    booking = bookingRepository.findBookingForItemByUserIdStatus(userId, BookingStatus.valueOf(status.name()));
            default -> throw new NotFoundException("Неверные параметры запроса");
        }

        return booking.stream().map(BookingMapper::toBookingDto).toList();
    }

    private Booking privateGet(long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронь не найдена"));
    }


}
