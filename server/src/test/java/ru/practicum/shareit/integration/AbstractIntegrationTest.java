package ru.practicum.shareit.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@AutoConfigureTestDatabase
public abstract class AbstractIntegrationTest {

    @Autowired
    protected UserService userService;

    @Autowired
    protected ItemService itemService;

    @Autowired
    protected BookingServiceImpl bookingService;

    @Autowired
    protected ItemRequestService itemRequestService;

    protected UserDto createTestUser(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userService.createUser(userDto);
    }

    protected ItemCreateDto createTestItemDto(String name, String description, boolean available) {
        ItemCreateDto itemDto = new ItemCreateDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemDto;
    }

    protected BookingCreateDto createTestBookingDto(Long itemId, LocalDateTime start, LocalDateTime end) {
        BookingCreateDto bookingDto = new BookingCreateDto();
        bookingDto.setItemId(itemId);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        return bookingDto;
    }

    protected ItemRequestDto createTestItemRequestDto(String description) {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription(description);
        return requestDto;
    }
}