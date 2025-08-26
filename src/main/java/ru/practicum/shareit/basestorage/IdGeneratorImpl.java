package ru.practicum.shareit.basestorage;

import org.springframework.stereotype.Component;

@Component("inMemoryIdGenerator")
public class IdGeneratorImpl implements IdGenerator {
    private long current = 0L;

    @Override
    public long nextId() {
        return current++;
    }
}
