package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.Entity;

import java.time.LocalDateTime;

@Data
public class Booking implements Entity {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long item;
    private long booker;
    private BookingStatus status;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
