package booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingClient bookingClient;

    @InjectMocks
    private BookingController controller;

    private MockMvc mvc;

    private ObjectMapper mapper;

    private BookItemRequestDto bookItemRequestDto;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                .build();

        bookItemRequestDto = new BookItemRequestDto(1L,
                LocalDateTime.now().plusDays(1).withNano(0),
                LocalDateTime.now().plusDays(2).withNano(0));
    }

    @Test
    void createBooking() throws Exception {
        when(bookingClient.createBooking(anyLong(), any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(bookItemRequestDto));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookItemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookItemRequestDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookItemRequestDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookItemRequestDto.getEnd().toString())));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingClient.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().build());

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking() throws Exception {
        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(bookItemRequestDto));

        mvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId", is(bookItemRequestDto.getItemId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookItemRequestDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookItemRequestDto.getEnd().toString())));
    }

    @Test
    void getUserBookings() throws Exception {
        when(bookingClient.getUserBookings(anyLong(), any(BookingState.class), any(Integer.class), any(Integer.class)))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(bookItemRequestDto)));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId", is(bookItemRequestDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookItemRequestDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookItemRequestDto.getEnd().toString())));
    }

    @Test
    void getOwnerBookings() throws Exception {
        when(bookingClient.getOwnerBookings(anyLong(), any(BookingState.class), any(Integer.class), any(Integer.class)))
                .thenReturn(ResponseEntity.ok(Collections.singletonList(bookItemRequestDto)));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId", is(bookItemRequestDto.getItemId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookItemRequestDto.getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookItemRequestDto.getEnd().toString())));
    }
}