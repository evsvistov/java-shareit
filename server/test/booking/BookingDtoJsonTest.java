package booking;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = {BookingDtoJsonTest.Config.class})
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Configuration
    static class Config {
        @Bean
        public static ObjectMapper objectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper;
        }
    }

    @BeforeEach
    void setup() {
        JacksonTester.initFields(this, Config.objectMapper());
    }

    @Test
    void testSerialize() throws Exception {
        ItemDto itemDto = new ItemDto(19L, "Sample Item", "Sample Description", true, null, null, null, null);
        UserDto userDto = new UserDto(55L, "John Doe", "john@example.com");
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 9, 3, 19, 18, 52));
        bookingDto.setEnd(LocalDateTime.of(2024, 9, 4, 19, 18, 52));
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(Booking.BookingStatus.WAITING);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-09-03T19:18:52");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-09-04T19:18:52");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(19);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Sample Item");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Sample Description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(55);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("john@example.com");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"start\":\"2024-09-03T19:18:52\",\"end\":\"2024-09-04T19:18:52\",\"item\":{\"id\":19,\"name\":\"Sample Item\",\"description\":\"Sample Description\",\"available\":true},\"booker\":{\"id\":55,\"name\":\"John Doe\",\"email\":\"john@example.com\"},\"status\":\"WAITING\"}";

        BookingDto result = json.parse(content).getObject();

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 9, 3, 19, 18, 52));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 9, 4, 19, 18, 52));
        assertThat(result.getItem().getId()).isEqualTo(19L);
        assertThat(result.getItem().getName()).isEqualTo("Sample Item");
        assertThat(result.getItem().getDescription()).isEqualTo("Sample Description");
        assertThat(result.getItem().getAvailable()).isEqualTo(true);
        assertThat(result.getBooker().getId()).isEqualTo(55L);
        assertThat(result.getBooker().getName()).isEqualTo("John Doe");
        assertThat(result.getBooker().getEmail()).isEqualTo("john@example.com");
        assertThat(result.getStatus()).isEqualTo(Booking.BookingStatus.WAITING);
    }

    @Test
    void testSerializeWithNullId() throws Exception {
        ItemDto itemDto = new ItemDto(null, "Sample Item", "Sample Description", true, null, null, null, null);
        UserDto userDto = new UserDto(null, "John Doe", "john@example.com");
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.of(2024, 9, 3, 19, 18, 52));
        bookingDto.setEnd(LocalDateTime.of(2024, 9, 4, 19, 18, 52));
        bookingDto.setItem(itemDto);
        bookingDto.setBooker(userDto);
        bookingDto.setStatus(Booking.BookingStatus.WAITING);

        JsonContent<BookingDto> result = json.write(bookingDto);

        assertThat(result).doesNotHaveJsonPath("$.id");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2024-09-03T19:18:52");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2024-09-04T19:18:52");
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Sample Item");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Sample Description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("john@example.com");
    }

    @Test
    void testDeserializeWithMissingFields() throws Exception {
        String content = "{\"start\":\"2024-09-03T19:18:52\",\"end\":\"2024-09-04T19:18:52\",\"item\":{\"name\":\"Sample Item\"},\"booker\":{\"name\":\"John Doe\"},\"status\":\"WAITING\"}";

        BookingDto result = json.parse(content).getObject();

        assertThat(result.getId()).isNull();
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2024, 9, 3, 19, 18, 52));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2024, 9, 4, 19, 18, 52));
        assertThat(result.getItem().getId()).isNull();
        assertThat(result.getItem().getName()).isEqualTo("Sample Item");
        assertThat(result.getItem().getDescription()).isNull();
        assertThat(result.getItem().getAvailable()).isNull();
        assertThat(result.getBooker().getId()).isNull();
        assertThat(result.getBooker().getName()).isEqualTo("John Doe");
        assertThat(result.getBooker().getEmail()).isNull();
        assertThat(result.getStatus()).isEqualTo(Booking.BookingStatus.WAITING);
    }
}
