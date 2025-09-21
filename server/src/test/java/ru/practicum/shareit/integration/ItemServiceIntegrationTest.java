package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceIntegrationTest extends AbstractIntegrationTest {

    private UserDto owner;

    @BeforeEach
    void setUp() {
        owner = createTestUser("Владелец", "owner@example.com");
    }

    @Test
    void getAllItems_WithBookingsAndComments_Success() {
        ItemCreateDto item1 = createTestItemDto("Первый предмет", "Описание один", true);
        ItemCreateDto item2 = createTestItemDto("Второй предмет", "Описание два", true);

        ItemDto createdItem1 = itemService.crateItem(item1, owner.getId());
        ItemDto createdItem2 = itemService.crateItem(item2, owner.getId());

        UserDto booker = createTestUser("Арендатор", "booker@test.com");

        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto pastBooking = createTestBookingDto(
                createdItem1.getId(),
                now.minusDays(2),
                now.minusDays(1)
        );
        BookingDto createdPastBooking = bookingService.create(pastBooking, booker.getId());
        bookingService.confirmBooking(createdPastBooking.getId(), true, owner.getId());

        BookingCreateDto futureBooking = createTestBookingDto(
                createdItem1.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );
        bookingService.create(futureBooking, booker.getId());

        CommentDto comment = new CommentDto();
        comment.setText("Отличная вещь!");
        itemService.createComment(createdItem1.getId(), booker.getId(), comment);

        List<ItemWithBookingsDto> ownerItems = itemService.getAllItems(owner.getId(), 0, 10);

        assertEquals(2, ownerItems.size());

        ItemWithBookingsDto firstItem = ownerItems.stream()
                .filter(item -> item.getName().equals("Первый предмет"))
                .findFirst()
                .orElseThrow();

        assertNotNull(firstItem.getLastBooking());
        assertEquals(1, firstItem.getComments().size());
        assertEquals("Отличная вещь!", firstItem.getComments().get(0).getText());

        ItemWithBookingsDto secondItem = ownerItems.stream()
                .filter(item -> item.getName().equals("Второй предмет"))
                .findFirst()
                .orElseThrow();

        assertNull(secondItem.getLastBooking());
        assertNull(secondItem.getNextBooking());
        assertTrue(secondItem.getComments().isEmpty());
    }

    @Test
    void searchItems_WithDifferentCases_Success() {
        ItemCreateDto item1 = createTestItemDto("Дрель электрическая", "Мощная дрель", true);
        ItemCreateDto item2 = createTestItemDto("Отвертка", "дрель отвертка универсальная", true);
        ItemCreateDto item3 = createTestItemDto("Молоток", "Просто молоток", true);

        itemService.crateItem(item1, owner.getId());
        itemService.crateItem(item2, owner.getId());
        ItemDto unavailableItem = itemService.crateItem(item3, owner.getId());

        List<ItemDto> searchResult1 = itemService.searchItems("дрел", 0, 10);
        assertEquals(2, searchResult1.size());

        List<ItemDto> searchResult2 = itemService.searchItems("универсал", 0, 10);
        assertEquals(1, searchResult2.size());

        List<ItemDto> searchResult3 = itemService.searchItems("", 0, 10);
        assertTrue(searchResult3.isEmpty());

        ItemDto updateDto = new ItemDto();
        updateDto.setAvailable(false);
        itemService.updateItem(unavailableItem.getId(), owner.getId(), updateDto);

        List<ItemDto> searchResult4 = itemService.searchItems("молоток", 0, 10);
        assertTrue(searchResult4.isEmpty());
    }

    @Test
    void getItem_WithBookingsAndComments_Success() {
        ItemCreateDto itemDto = createTestItemDto("Тестовый предмет", "Описание", true);
        ItemDto createdItem = itemService.crateItem(itemDto, owner.getId());

        UserDto booker = createTestUser("Арендатор", "booker@test.com");

        LocalDateTime now = LocalDateTime.now();
        BookingCreateDto pastBooking = createTestBookingDto(
                createdItem.getId(),
                now.minusDays(2),
                now.minusDays(1)
        );
        BookingDto createdPastBooking = bookingService.create(pastBooking, booker.getId());
        bookingService.confirmBooking(createdPastBooking.getId(), true, owner.getId());

        BookingCreateDto futureBooking = createTestBookingDto(
                createdItem.getId(),
                now.plusDays(1),
                now.plusDays(2)
        );
        bookingService.create(futureBooking, booker.getId());

        CommentDto comment = new CommentDto();
        comment.setText("Отличный предмет!");
        itemService.createComment(createdItem.getId(), booker.getId(), comment);

        ItemWithBookingsDto itemWithBookings = itemService.getItem(createdItem.getId());

        assertEquals(1, itemWithBookings.getComments().size());
        assertEquals("Отличный предмет!", itemWithBookings.getComments().get(0).getText());
    }

    @Test
    void createAndGetItem_Success() {
        ItemCreateDto itemDto = createTestItemDto("Новый предмет", "Описание предмета", true);
        ItemDto createdItem = itemService.crateItem(itemDto, owner.getId());

        assertNotNull(createdItem.getId());
        assertEquals(itemDto.getName(), createdItem.getName());
        assertEquals(itemDto.getDescription(), createdItem.getDescription());
        assertEquals(itemDto.getAvailable(), createdItem.getAvailable());

        ItemWithBookingsDto retrievedItem = itemService.getItem(createdItem.getId());
        assertEquals(createdItem.getId(), retrievedItem.getId());
        assertEquals(createdItem.getName(), retrievedItem.getName());
    }

    @Test
    void updateItem_Success() {
        ItemCreateDto itemDto = createTestItemDto("Старый предмет", "Старое описание", true);
        ItemDto createdItem = itemService.crateItem(itemDto, owner.getId());

        ItemDto updateDto = new ItemDto();
        updateDto.setName("Обновленный предмет");
        updateDto.setDescription("Новое описание");
        updateDto.setAvailable(false);

        ItemDto updatedItem = itemService.updateItem(createdItem.getId(), owner.getId(), updateDto);

        assertEquals("Обновленный предмет", updatedItem.getName());
        assertEquals("Новое описание", updatedItem.getDescription());
        assertFalse(updatedItem.getAvailable());
    }

    @Test
    void searchItems_Success() {
        ItemCreateDto item1 = createTestItemDto("Поисковый предмет", "Описание для поиска", true);
        ItemCreateDto item2 = createTestItemDto("Другая вещь", "Тоже для поиска", true);

        itemService.crateItem(item1, owner.getId());
        itemService.crateItem(item2, owner.getId());

        List<ItemDto> searchResults = itemService.searchItems("поиск", 0, 10);

        assertEquals(2, searchResults.size());
    }

    @Test
    void createComment_Success() {
        UserDto booker = createTestUser("Комментатор", "commentator@example.com");
        ItemCreateDto itemDto = createTestItemDto("Предмет для комментария", "Описание", true);
        ItemDto item = itemService.crateItem(itemDto, owner.getId());

        BookingCreateDto bookingDto = createTestBookingDto(
                item.getId(),
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1)
        );
        BookingDto booking = bookingService.create(bookingDto, booker.getId());
        bookingService.confirmBooking(booking.getId(), true, owner.getId());

        CommentDto commentDto = new CommentDto();
        commentDto.setText("Тестовый комментарий");

        CommentDto createdComment = itemService.createComment(item.getId(), booker.getId(), commentDto);

        assertNotNull(createdComment.getId());
        assertEquals(commentDto.getText(), createdComment.getText());
        assertEquals(booker.getName(), createdComment.getAuthorName());
    }
}