package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemRequestServiceIntegrationTest extends AbstractIntegrationTest {

    private UserDto requester;

    @BeforeEach
    void setUp() {
        requester = createTestUser("запросящий", "requester@example.com");
    }

    @Test
    void getOwnRequests_Success() {
        ItemRequestDto request1 = createTestItemRequestDto("первый запрос");
        ItemRequestDto request2 = createTestItemRequestDto("второй запрос");

        itemRequestService.create(request1, requester.getId());
        itemRequestService.create(request2, requester.getId());

        List<ItemRequestDto> ownRequests = itemRequestService.getOwnRequests(requester.getId());

        assertEquals(2, ownRequests.size());
        assertTrue(ownRequests.stream()
                .anyMatch(request -> request.getDescription().equals("первый запрос")));
        assertTrue(ownRequests.stream()
                .anyMatch(request -> request.getDescription().equals("второй запрос")));
    }
}