package ru.practicum.shareit.request;

import lombok.Data;
import ru.practicum.shareit.Entity;

import java.time.LocalDateTime;

@Data
public class ItemRequest implements Entity {
    private long id;
    private String description;
    private long requestor;
    private LocalDateTime created;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
