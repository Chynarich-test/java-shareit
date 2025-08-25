package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.basestorage.IdGenerator;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final IdGenerator idGenerator;
    private final ItemMapper itemMapper;
    private final UserStorage userStorage;

    public ItemServiceImpl(@Qualifier("itemInMemoryStorage") ItemStorage itemStorage,
                           @Qualifier("inMemoryIdGenerator") IdGenerator idGenerator, ItemMapper itemMapper,
                           @Qualifier("userInMemoryStorage") UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.idGenerator = idGenerator;
        this.itemMapper = itemMapper;
        this.userStorage = userStorage;
    }

    @Override
    public ItemDto getItem(long id) {
        return itemMapper.toItemDto(itemStorage.getOne(id));
    }

    @Override
    public ItemDto crateItem(ItemDto item, long userId) {
        userStorage.getOne(userId);
        Item newItem = itemMapper.toItem(item);
        newItem.setId(idGenerator.nextId());
        newItem.setOwner(userId);
        return itemMapper.toItemDto(itemStorage.create(newItem));
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, ItemDto itemDto) {
        Item existingItem = itemStorage.getOne(itemId);

        if (existingItem.getOwner() != userId) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return itemMapper.toItemDto(itemStorage.update(existingItem));
    }

    @Override
    public List<ItemDto> getAllItems(long userId) {
        return itemStorage.getAllItems(userId).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) return List.of();
        return itemStorage.searchItems(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }
}
