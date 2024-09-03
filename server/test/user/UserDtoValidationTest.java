package user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserDtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidUserDto() {
        UserDto userDto = new UserDto(1L, "John Doe", "john@example.com");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void testInvalidUserDto() {
        UserDto userDto = new UserDto(1L, "", "invalid-email");
        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertThat(violations).hasSize(2);
        assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("name") &&
                violation.getMessage().equals("Имя не может быть пустым"));
        assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("email") &&
                violation.getMessage().equals("Электронная почта должна быть корректной и содержать символ @"));
    }
}