package item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testItemDtoValid() {
        ItemDto itemDto = new ItemDto(1L, "Item Name", "Item Description", true, null, null, null, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertTrue(violations.isEmpty(), "ItemDto должен быть валидным");
    }

    @Test
    void testItemDtoInvalid() {
        ItemDto itemDto = new ItemDto(1L, "", "", null, null, null, null, null);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertEquals(3, violations.size(), "ItemDto должен иметь 3 ошибки проверки");
    }
}

