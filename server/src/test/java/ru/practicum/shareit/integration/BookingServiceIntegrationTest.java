package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingRequestType;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingServiceIntegrationTest extends AbstractIntegrationTest {

    private UserDto owner;
    private UserDto booker;
    private ItemDto item;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        owner = createTestUser("Хозяин", "owner@example.com");
        booker = createTestUser("Бронировщик", "booker@example.com");

        ItemCreateDto itemDto = createTestItemDto("Тестовый айтем", "Тестовый дескришион", true);
        item = itemService.crateItem(itemDto, owner.getId());
    }

    @Test
    void createAndGetBooking_Success() {
        BookingCreateDto bookingDto = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );

        BookingDto createdBooking = bookingService.create(bookingDto, booker.getId());

        assertNotNull(createdBooking.getId());
        assertEquals(BookingStatus.WAITING, createdBooking.getStatus());
        assertEquals(item.getId(), createdBooking.getItem().getId());
        assertEquals(booker.getId(), createdBooking.getBooker().getId());

        BookingDto retrievedBooking = bookingService.getBooking(createdBooking.getId(), booker.getId());
        assertEquals(createdBooking.getId(), retrievedBooking.getId());
        assertEquals(createdBooking.getStatus(), retrievedBooking.getStatus());
    }

    @Test
    void confirmBooking_Success() {
        BookingCreateDto bookingDto = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );

        BookingDto createdBooking = bookingService.create(bookingDto, booker.getId());
        BookingDto confirmedBooking = bookingService.confirmBooking(createdBooking.getId(), true, owner.getId());

        assertEquals(BookingStatus.APPROVED, confirmedBooking.getStatus());
    }

    @Test
    void getAllBooking_Success() {
        BookingCreateDto booking1 = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );
        BookingCreateDto booking2 = createTestBookingDto(
                item.getId(),
                now.plusDays(3),
                now.plusDays(4)
        );

        bookingService.create(booking1, booker.getId());
        bookingService.create(booking2, booker.getId());

        List<BookingDto> bookings = bookingService.getAllBooking(booker.getId(), BookingRequestType.ALL, 0, 10);

        assertEquals(2, bookings.size());
    }

    @Test
    void getAllItemsBookings_Success() {
        BookingCreateDto bookingDto = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );

        bookingService.create(bookingDto, booker.getId());

        List<BookingDto> ownerBookings = bookingService.getAllItemsBookings(owner.getId(), BookingRequestType.ALL);

        assertEquals(1, ownerBookings.size());
        assertEquals(item.getId(), ownerBookings.get(0).getItem().getId());
    }

    @Test
    void createBooking_ItemNotAvailable_ThrowsException() {
        ItemCreateDto itemDto = createTestItemDto("Недоступный предмет", "Описание", false);
        ItemDto unavailableItem = itemService.crateItem(itemDto, owner.getId());

        BookingCreateDto bookingDto = createTestBookingDto(
                unavailableItem.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDto, booker.getId()));
    }


    @Test
    void confirmBooking_NotByOwner_ThrowsException() {
        BookingCreateDto bookingDto = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );

        BookingDto createdBooking = bookingService.create(bookingDto, booker.getId());

        UserDto randomUser = createTestUser("Случайный", "random@example.com");
        assertThrows(ValidationException.class,
                () -> bookingService.confirmBooking(createdBooking.getId(), true, randomUser.getId()));
    }

    @Test
    void getAllBooking_DifferentStates_Success() {
        BookingCreateDto futureBooking = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );
        bookingService.create(futureBooking, booker.getId());

        List<BookingDto> futureBookings = bookingService.getAllBooking(
                booker.getId(), BookingRequestType.FUTURE, 0, 10);
        assertEquals(1, futureBookings.size());

        List<BookingDto> waitingBookings = bookingService.getAllBooking(
                booker.getId(), BookingRequestType.WAITING, 0, 10);
        assertEquals(1, waitingBookings.size());

        BookingDto toReject = bookingService.create(createTestBookingDto(
                item.getId(),
                now.plusDays(3),
                now.plusDays(4)
        ), booker.getId());
        bookingService.confirmBooking(toReject.getId(), false, owner.getId());

        List<BookingDto> rejectedBookings = bookingService.getAllBooking(
                booker.getId(), BookingRequestType.REJECTED, 0, 10);
        assertEquals(1, rejectedBookings.size());
    }

    @Test
    void getBooking_ByNotOwnerOrBooker_ThrowsException() {
        BookingCreateDto bookingDto = createTestBookingDto(
                item.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );

        BookingDto createdBooking = bookingService.create(bookingDto, booker.getId());
        UserDto randomUser = createTestUser("Случайный", "random@example.com");

        assertThrows(NotFoundException.class,
                () -> bookingService.getBooking(createdBooking.getId(), randomUser.getId()));
    }
}