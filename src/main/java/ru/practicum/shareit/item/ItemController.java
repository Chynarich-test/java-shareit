package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingsDto getItem(@PathVariable long itemId) {
        return itemService.getItem(itemId);
    }

    @GetMapping
    public List<ItemWithBookingsDto> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable("itemId") long itemId,
                          @RequestHeader("X-Sharer-User-Id") int userId,
                          @RequestBody ItemDto itemDto) {
        return itemService.updateItem(itemId, userId, itemDto);
    }

    @PostMapping
    public ItemDto create(@Valid @RequestBody ItemDto newElement,
                          @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.crateItem(newElement, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable("itemId") long itemId,
                                    @RequestBody CommentDto newElement,
                                    @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.createComment(itemId, userId, newElement);
    }
}
