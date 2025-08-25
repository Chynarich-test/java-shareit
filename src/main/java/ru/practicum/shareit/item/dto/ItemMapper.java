package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestServiceImpl;

@Component
public class ItemMapper {
    private final ItemRequestServiceImpl itemRequestService;

    public ItemMapper(ItemRequestServiceImpl itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    public ItemDto toItemDto(Item item) {
        ItemRequest request = null;
        if (item.getRequest() != 0) {
            request = itemRequestService.getItemRequest(item.getRequest());
        }

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                request
        );
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
