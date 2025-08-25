package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.basestorage.BaseStorage;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage extends BaseStorage<Item> {

    List<Item> getAllItems(long ownerId);

    List<Item> searchItems(String text);
}
