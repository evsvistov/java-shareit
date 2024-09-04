package request;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.practicum.shareit.request.service.ItemRequestService;

import static org.mockito.Mockito.mock;

@TestConfiguration
class ItemRequestControllerTestConfig {
    @Bean
    public ItemRequestService itemRequestService() {
        return mock(ItemRequestService.class);
    }
}
