package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.base.AppConstants;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingServiceImpl bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingDto bookingDto;
    private BookingCreateDto bookingCreateDto;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(now.plusDays(1));
        bookingDto.setEnd(now.plusDays(2));
        bookingDto.setStatus(BookingStatus.WAITING);

        bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(1L);
        bookingCreateDto.setStart(now.plusDays(1));
        bookingCreateDto.setEnd(now.plusDays(2));
    }

    @Test
    void createBooking_ValidBooking_ReturnsCreatedBooking() throws Exception {
        when(bookingService.create(any(BookingCreateDto.class), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header(AppConstants.USER_ID_HEADER, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void confirmBooking_ValidConfirmation_ReturnsUpdatedBooking() throws Exception {
        BookingDto confirmedBooking = new BookingDto();
        confirmedBooking.setId(bookingDto.getId());
        confirmedBooking.setStatus(BookingStatus.APPROVED);

        when(bookingService.confirmBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(confirmedBooking);

        mockMvc.perform(patch("/bookings/1")
                        .header(AppConstants.USER_ID_HEADER, "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(confirmedBooking.getId()))
                .andExpect(jsonPath("$.status").value(confirmedBooking.getStatus().toString()));
    }

    @Test
    void getBooking_ExistingBooking_ReturnsBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header(AppConstants.USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getAllBooking_ReturnsBookingList() throws Exception {
        List<BookingDto> bookings = Collections.singletonList(bookingDto);
        when(bookingService.getAllBooking(anyLong(), any(BookingRequestType.class), anyInt(), anyInt()))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header(AppConstants.USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }

    @Test
    void getAllItemsBookings_ReturnsBookingList() throws Exception {
        List<BookingDto> bookings = Collections.singletonList(bookingDto);
        when(bookingService.getAllItemsBookings(anyLong(), any(BookingRequestType.class)))
                .thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header(AppConstants.USER_ID_HEADER, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDto.getStatus().toString()));
    }
}