package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.base.AppConstants;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingServiceImpl bookingService;

    public BookingController(BookingServiceImpl bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingCreateDto bookingCreateDto,
                             @RequestHeader(AppConstants.USER_ID_HEADER) int userId) {
        return bookingService.create(bookingCreateDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto confirmBooking(@PathVariable("bookingId") long bookingId,
                                     @RequestParam("approved") boolean status,
                                     @RequestHeader(AppConstants.USER_ID_HEADER) int userId) {
        return bookingService.confirmBooking(bookingId, status, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable("bookingId") long bookingId,
                                 @RequestHeader(AppConstants.USER_ID_HEADER) int userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBooking(@RequestHeader(AppConstants.USER_ID_HEADER) int userId,
                                          @RequestParam(name = "state", required = false, defaultValue = "ALL") BookingRequestType state) {
        return bookingService.getAllBooking(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllItemsBookings(@RequestHeader(AppConstants.USER_ID_HEADER) int userId,
                                                @RequestParam(name = "state", required = false, defaultValue = "ALL") BookingRequestType state) {
        return bookingService.getAllItemsBookings(userId, state);
    }
}
