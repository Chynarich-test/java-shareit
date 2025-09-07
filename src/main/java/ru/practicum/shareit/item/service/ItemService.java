package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemWithBookingsDto getItem(long id);

    ItemDto crateItem(ItemDto item, long userId);

    ItemDto updateItem(long itemId, long userId, ItemDto itemDto);

    List<ItemWithBookingsDto> getAllItems(long userId);

    List<ItemDto> searchItems(String text);
}
