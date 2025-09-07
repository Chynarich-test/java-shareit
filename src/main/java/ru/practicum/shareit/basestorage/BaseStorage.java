package ru.practicum.shareit.basestorage;

import ru.practicum.shareit.BaseEntity;

public interface BaseStorage<T extends BaseEntity> {
    T create(T element);

    T update(T newElement);

    T getOne(long id);

    void delete(long id);
}