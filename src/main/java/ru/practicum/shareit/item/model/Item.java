package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.Entity;

@Data
@Builder
public class Item implements Entity {
    private long id;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private boolean available;
    private long owner;
    private long request;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
