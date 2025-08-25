package ru.practicum.shareit.basestorage;

import ru.practicum.shareit.Entity;

public interface BaseStorage<T extends Entity> {
    T create(T element);

    T update(T newElement);

    T getOne(long id);

    void delete(long id);
}