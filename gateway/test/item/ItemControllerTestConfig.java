package item;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.practicum.shareit.item.service.ItemService;

import static org.mockito.Mockito.mock;

@TestConfiguration
class ItemControllerTestConfig {
    @Bean
    public ItemService itemService() {
        return mock(ItemService.class);
    }
}