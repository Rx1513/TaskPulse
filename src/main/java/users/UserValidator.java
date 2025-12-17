package users;

import java.util.regex.Pattern;

/**
 * Simple validation helpers for user data.
 */
public final class UserValidator {
    private static final Pattern EMAIL_REGEX =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Letters, digits, spaces, underscore, dash; length 2-100 (no dots)
    private static final Pattern NAME_REGEX = Pattern.compile("^[\\p{L}0-9 _-]{2,100}$");

    private UserValidator() {}

    public static void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Электронная почта не может быть пустой");
        }

        if (!EMAIL_REGEX.matcher(email).matches()) {
            throw new IllegalArgumentException("Формат почты неверный (ожидаемый формат: user@example.com)");
        }
    }

    public static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя пользователя не может быть пустым!");
        }

        if (!NAME_REGEX.matcher(name).matches()) {
            throw new IllegalArgumentException("Имя пользователя может содержать только латинские буквы, числа, подчёркивания и дефисы!");
        }
    }
}
