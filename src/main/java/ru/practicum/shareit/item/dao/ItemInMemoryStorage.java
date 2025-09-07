package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

@Repository("itemInMemoryStorage")
public class ItemInMemoryStorage implements ItemStorage {
    private final List<Item> itemList = new ArrayList<>();

    @Override
    public Item create(Item element) {
        itemList.add(element);
        return getOne(element.getId());
    }

    @Override
    public Item update(Item newElement) {
        Item updateItem = getOne(newElement.getId());
        updateItem.setName(newElement.getName());
        updateItem.setDescription(newElement.getDescription());
        updateItem.setAvailable(newElement.isAvailable());
        updateItem.setOwner(newElement.getOwner());
        updateItem.setRequest(newElement.getRequest());
        return updateItem;
    }

    @Override
    public Item getOne(long id) {
        return itemList.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + id + " не найдена"));
    }

    @Override
    public void delete(long id) {
        itemList.remove(getOne(id));
    }

    @Override
    public List<Item> getAllItems(long ownerId) {
//        return itemList.stream()
//                .filter(item -> item.getOwner() == ownerId)
//                .toList();
        return List.of();
    }

    @Override
    public List<Item> searchItems(String text) {
        String textLower = text.toLowerCase();
        return itemList.stream()
                .filter(Item::isAvailable)
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(textLower))
                                || (item.getDescription() != null
                                && item.getDescription().toLowerCase().contains(textLower))
                )
                .toList();
    }
}
