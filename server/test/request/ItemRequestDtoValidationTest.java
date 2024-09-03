package request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemRequestDtoValidationTest {

    private static Validator validator;

    @BeforeAll
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testItemRequestDtoValid() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("Request description");

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "ItemRequestDto должен быть валидным");
    }

    @Test
    void testItemRequestDtoInvalid() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setDescription("");

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(dto);
        assertEquals(1, violations.size(), "ItemRequestDto должен быть с одной ошибкой валидации");
    }
}

