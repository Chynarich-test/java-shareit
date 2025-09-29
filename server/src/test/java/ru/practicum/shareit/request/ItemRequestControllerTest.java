package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.base.AppConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("Тестовый запрос");
        itemRequestDto.setCreated(LocalDateTime.now());
    }

    @Test
    void createRequest_ValidRequest_ReturnsCreatedRequest() throws Exception {
        when(itemRequestService.create(any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header(AppConstants.USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));
    }

    @Test
    void getOwnRequests_ReturnsRequestList() throws Exception {
        List<ItemRequestDto> requests = Collections.singletonList(itemRequestDto);
        when(itemRequestService.getOwnRequests(anyLong()))
                .thenReturn(requests);

        mockMvc.perform(get("/requests")
                        .header(AppConstants.USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()));
    }

    @Test
    void getOtherUsersRequests_ReturnsRequestList() throws Exception {
        List<ItemRequestDto> requests = Collections.singletonList(itemRequestDto);
        when(itemRequestService.getOtherUsersRequests(anyLong()))
                .thenReturn(requests);

        mockMvc.perform(get("/requests/all")
                        .header(AppConstants.USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDto.getDescription()));
    }

    @Test
    void getRequestById_ExistingRequest_ReturnsRequest() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header(AppConstants.USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()));
    }
}