package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        if (booking == null) return null;
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getBooker() == null ? null :
                        BookerDto.builder()
                                .id(booking.getBooker().getId())
                                .build())
                .item(booking.getItem() == null ? null :
                        BookingItemDto.builder()
                                .id(booking.getItem().getId())
                                .name(booking.getItem().getName())
                                .build())
                .build();
    }

    public static BookingShortDto toBookingShortDto(Booking booking) {
        if (booking == null) return null;
        return BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public static Booking fromCreateDto(BookingCreateDto dto, Item item, User booker) {
        if (dto == null) return null;
        return Booking.builder()
                .start(dto.getStart())
                .end(dto.getEnd())
                .item(item)
                .booker(booker)
                .build();
    }


}
