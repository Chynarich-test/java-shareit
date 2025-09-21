package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void createAndGetRequestById_Success() {
        ItemRequestDto requestDto = createTestItemRequestDto("тестовый запрос");
        ItemRequestDto createdRequest = itemRequestService.create(requestDto, requester.getId());

        assertNotNull(createdRequest.getId());
        assertEquals(requestDto.getDescription(), createdRequest.getDescription());
        assertNotNull(createdRequest.getCreated());

        ItemRequestDto retrievedRequest = itemRequestService.getRequestById(createdRequest.getId(), requester.getId());
        assertEquals(createdRequest.getId(), retrievedRequest.getId());
        assertEquals(createdRequest.getDescription(), retrievedRequest.getDescription());
    }

    @Test
    void getOtherUsersRequests_Success() {
        UserDto otherUser = createTestUser("другой пользователь", "other@example.com");
        ItemRequestDto request = createTestItemRequestDto("тестовый запрос");
        
        itemRequestService.create(request, requester.getId());

        List<ItemRequestDto> otherRequests = itemRequestService.getOtherUsersRequests(otherUser.getId());
        
        assertFalse(otherRequests.isEmpty());
        assertEquals(request.getDescription(), otherRequests.get(0).getDescription());
    }
}