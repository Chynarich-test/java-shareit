package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
}