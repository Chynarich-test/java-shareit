package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemServiceIntegrationTest extends AbstractIntegrationTest {

    private UserDto owner;

    @BeforeEach
    void setUp() {
        owner = createTestUser("Владелец", "owner@example.com");
    }

    @Test
    void getAllItems_Success() {
        ItemCreateDto item1 = createTestItemDto("Первый предмет", "Описание один", true);
        ItemCreateDto item2 = createTestItemDto("Второй предмет", "Описание два", true);

        itemService.crateItem(item1, owner.getId());
        itemService.crateItem(item2, owner.getId());

        List<ItemWithBookingsDto> ownerItems = itemService.getAllItems(owner.getId(), 0, 10);

        assertEquals(2, ownerItems.size());
        assertTrue(ownerItems.stream().anyMatch(item -> item.getName().equals("Первый предмет")));
        assertTrue(ownerItems.stream().anyMatch(item -> item.getName().equals("Второй предмет")));
    }
}