package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.List;

public interface ItemService {
    ItemWithBookingsDto getItem(long id);

    ItemDto crateItem(ItemCreateDto item, long userId);

    ItemDto updateItem(long itemId, long userId, ItemDto itemDto);

    List<ItemWithBookingsDto> getAllItems(long userId, int from, int size);

    List<ItemDto> searchItems(String text, int from, int size);
}
