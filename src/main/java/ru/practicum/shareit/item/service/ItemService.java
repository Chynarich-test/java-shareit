package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItem(long id);

    ItemDto crateItem(ItemDto item, long userId);

    ItemDto updateItem(long itemId, long userId, ItemDto itemDto);

    List<ItemDto> getAllItems(long userId);

    List<ItemDto> searchItems(String text);
}
