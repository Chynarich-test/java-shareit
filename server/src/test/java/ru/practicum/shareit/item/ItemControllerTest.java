package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.base.AppConstants;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemServiceImpl itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;
    private ItemCreateDto itemCreateDto;
    private ItemWithBookingsDto itemWithBookingsDto;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setName("Тестовый предмет");
        itemDto.setDescription("Тестовое описание");
        itemDto.setAvailable(true);

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("Тестовый предмет");
        itemCreateDto.setDescription("Тестовое описание");
        itemCreateDto.setAvailable(true);

        itemWithBookingsDto = new ItemWithBookingsDto();
        itemWithBookingsDto.setId(1L);
        itemWithBookingsDto.setName("Тестовый предмет");
        itemWithBookingsDto.setDescription("Тестовое описание");
        itemWithBookingsDto.setAvailable(true);

        commentDto = new CommentDto();
        commentDto.setText("Тестовый комент");
    }

    @Test
    void getItem_ExistingItem_ReturnsItem() throws Exception {
        when(itemService.getItem(anyLong()))
                .thenReturn(itemWithBookingsDto);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemWithBookingsDto.getId()))
                .andExpect(jsonPath("$.name").value(itemWithBookingsDto.getName()))
                .andExpect(jsonPath("$.description").value(itemWithBookingsDto.getDescription()));
    }

    @Test
    void getAllItems_ReturnsItemList() throws Exception {
        List<ItemWithBookingsDto> items = Collections.singletonList(itemWithBookingsDto);
        when(itemService.getAllItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(items);

        mockMvc.perform(get("/items")
                        .header(AppConstants.USER_ID_HEADER, 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemWithBookingsDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemWithBookingsDto.getName()));
    }

    @Test
    void searchItems_ReturnsMatchingItems() throws Exception {
        List<ItemDto> items = Collections.singletonList(itemDto);
        when(itemService.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(items);

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()));
    }

    @Test
    void updateItem_ValidUpdate_ReturnsUpdatedItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header(AppConstants.USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    void createItem_ValidItem_ReturnsCreatedItem() throws Exception {
        when(itemService.crateItem(any(ItemCreateDto.class), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header(AppConstants.USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()));
    }

    @Test
    void createComment_ValidComment_ReturnsCreatedComment() throws Exception {
        when(itemService.createComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(AppConstants.USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value(commentDto.getText()));
    }
}