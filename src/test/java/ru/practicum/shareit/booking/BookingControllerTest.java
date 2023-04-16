package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.WrongStatusException;
import ru.practicum.shareit.item.projection.ItemShort;
import ru.practicum.shareit.user.projection.UserShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    private final LocalDateTime start = LocalDateTime.now();
    private final LocalDateTime end = LocalDateTime.now();
    private final BookingRequestDto bookingRequest = new BookingRequestDto();
    private BookingResponseDto bookingResponse;

    @BeforeEach
    void setUp() {
        setBookingRequest();
        setBookingResponse();
    }

    @Test
    void testAddBooking() throws Exception {
        Mockito.when(bookingService.addBooking(Mockito.anyLong(), Mockito.any())).thenReturn(bookingResponse);
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponse.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId().intValue())))
                .andExpect(jsonPath("$.item.name", is(bookingResponse.getItem().getName())));
    }

    @Test
    void testSetBookingStatus() throws Exception {
        Mockito.when(bookingService.setBookingStatus(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyBoolean()))
                .thenReturn(bookingResponse);
        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponse.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId().intValue())))
                .andExpect(jsonPath("$.item.name", is(bookingResponse.getItem().getName())));
    }

    @Test
    void testGetBookingById() throws Exception {
        Mockito.when(bookingService.getBookingById(Mockito.anyLong(), Mockito.anyLong())).thenReturn(bookingResponse);
        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingResponse.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId().intValue())))
                .andExpect(jsonPath("$.item.name", is(bookingResponse.getItem().getName())));
    }

    @Test
    void testGetAllBookingsOfBookerByState() throws Exception {
        Mockito.when(bookingService.getAllBookingsOfBookerByState(Mockito.anyLong(), Mockito.anyString(),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(bookingResponse));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingResponse.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponse.getBooker().getId().intValue())))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponse.getItem().getId().intValue())))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponse.getItem().getName())));
    }

    @Test
    void testGetAllBookingsWrongStatus() throws Exception {
        Mockito.when(bookingService.getAllBookingsOfBookerByState(Mockito.anyLong(), Mockito.anyString(),
                Mockito.anyInt(), Mockito.anyInt())).thenThrow(new WrongStatusException("Wrong status"));
        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "WRONG")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error", equalTo("Wrong status")));
    }

    @Test
    void testGetAllBookingsOfOwnerByState() throws Exception {
        Mockito.when(bookingService.getAllBookingsOfOwnerByState(Mockito.anyLong(), Mockito.anyString(),
                Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(bookingResponse));
        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingResponse.getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingResponse.getStatus().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponse.getBooker().getId().intValue())))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponse.getItem().getId().intValue())))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponse.getItem().getName())));
    }

    private void setBookingRequest() {
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(start);
        bookingRequest.setEnd(end);
    }

    private void setBookingResponse() {
        bookingResponse = BookingResponseDto.builder()
                .id(1L)
                .start(start)
                .end(end)
                .status(BookingStatus.WAITING)
                .booker(new UserShort(1L))
                .item(new ItemShort(1L, "item"))
                .build();
    }
}
