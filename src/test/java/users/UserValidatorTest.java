package users;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.Test;

class UserValidatorTest {

    @Test
    void acceptsValidEmailAndName() {
        assertThatCode(() -> UserValidator.validateEmail("john.doe@example.com"))
                .doesNotThrowAnyException();
        assertThatCode(() -> UserValidator.validateName("John Doe"))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsInvalidEmail() {
        assertThatThrownBy(() -> UserValidator.validateEmail("bad-email"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> UserValidator.validateEmail("no_at_symbol.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsInvalidName() {
        assertThatThrownBy(() -> UserValidator.validateName(""))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> UserValidator.validateName("!@#$"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
