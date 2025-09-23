package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {

    @Test
    void toItemDto() {
        Item item = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Мощная дрель")
                .available(true)
                .request(null)
                .build();

        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.isAvailable(), itemDto.getAvailable());
        assertNull(itemDto.getRequest());
    }

    @Test
    void toItem() {
        ItemRequestDto requestDto = ItemRequestDto.builder().id(10L).build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Отвертка")
                .description("Крестовая отвертка")
                .available(true)
                .request(requestDto)
                .build();

        Item item = ItemMapper.toItem(itemDto);

        assertEquals(itemDto.getId(), item.getId());
        assertEquals(itemDto.getName(), item.getName());
        assertEquals(itemDto.getDescription(), item.getDescription());
        assertEquals(itemDto.getAvailable(), item.isAvailable());
        assertNotNull(item.getRequest());
        assertEquals(requestDto.getId(), item.getRequest().getId());
    }

    @Test
    void toItemWithNullRequest() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Отвертка")
                .description("Крестовая отвертка")
                .available(true)
                .request(null)
                .build();
        Item item = ItemMapper.toItem(itemDto);
        assertNull(item.getRequest());
    }

    @Test
    void createToItem() {
        ItemCreateDto createDto = ItemCreateDto.builder()
                .name("Молоток")
                .description("Тяжелый молоток")
                .available(false)
                .build();

        Item item = ItemMapper.createToItem(createDto);

        assertEquals(createDto.getName(), item.getName());
        assertEquals(createDto.getDescription(), item.getDescription());
        assertEquals(createDto.getAvailable(), item.isAvailable());
        assertNull(item.getId());
    }

    @Test
    void toItemWithBookingsDto() {
        Item item = Item.builder()
                .id(1L)
                .name("Пила")
                .description("Острая пила")
                .available(true)
                .request(new ItemRequest())
                .build();

        LocalDateTime now = LocalDateTime.now();

        BookingShortDto lastBooking = BookingShortDto.builder()
                .id(1L)
                .bookerId(10L)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .build();

        BookingShortDto nextBooking = BookingShortDto.builder()
                .id(2L)
                .bookerId(11L)
                .start(now.plusDays(1))
                .end(now.plusDays(2))
                .build();

        CommentDto comment = CommentDto.builder()
                .id(1L)
                .text("Отличная пила!")
                .authorName("Петр")
                .created(now)
                .build();
        List<CommentDto> comments = List.of(comment);

        ItemWithBookingsDto dto = ItemMapper.toItemWithBookingsDto(item, lastBooking, nextBooking, comments);

        assertEquals(item.getId(), dto.getId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(item.getDescription(), dto.getDescription());
        assertEquals(item.isAvailable(), dto.getAvailable());
        assertEquals(lastBooking, dto.getLastBooking());
        assertEquals(nextBooking, dto.getNextBooking());
        assertEquals(1, dto.getComments().size());
        assertEquals(comment.getText(), dto.getComments().get(0).getText());
    }

    @Test
    void toItemAnswerDto() {
        User owner = new User();
        owner.setId(5L);

        Item item = Item.builder()
                .id(1L)
                .name("Шуруповерт")
                .owner(owner)
                .build();

        ItemAnswerDto dto = ItemMapper.toItemAnswerDto(item);

        assertEquals(item.getId(), dto.getItemId());
        assertEquals(item.getName(), dto.getName());
        assertEquals(owner.getId(), dto.getOwnerId());
    }
}
