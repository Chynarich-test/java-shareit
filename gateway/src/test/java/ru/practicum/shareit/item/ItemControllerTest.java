package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collections;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    @Test
    void getItem() throws Exception {
        when(itemClient.getItem(anyLong()))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        mvc.perform(get("/items/{itemId}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItems() throws Exception {
        when(itemClient.getAllItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mvc.perform(get("/items")
                        .header(USER_ID_HEADER, 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems() throws Exception {
        when(itemClient.searchItems(anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(Collections.emptyList()));

        mvc.perform(get("/items/search")
                        .param("text", "search query")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void update() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Обновляемый предмет");
        itemDto.setDescription("Реально ведь обновляемый предмет");
        itemDto.setAvailable(true);
        when(itemClient.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(itemDto));

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void create() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto("New Item", "New Description", true, null);
        when(itemClient.createItem(any(ItemCreateDto.class), anyLong()))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        mvc.perform(post("/items")
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Это крутой предмет");
        when(itemClient.createComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(ResponseEntity.ok(Map.of("id", 1L)));

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(USER_ID_HEADER, 1L)
                        .content(mapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}