package booking;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.practicum.shareit.booking.service.BookingService;

import static org.mockito.Mockito.mock;

@TestConfiguration
class BookingControllerTestConfig {
    @Bean
    public BookingService bookingService() {
        return mock(BookingService.class);
    }
}
