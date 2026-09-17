package campusconnect.util;

import campusconnect.exception.InvalidDataException;
import java.util.regex.Pattern;

/** Shared validation routines used across every service before data is persisted. */
public final class InputValidator {
    private InputValidator() {}

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$");

    public static void requireNonEmpty(String value, String fieldName) throws InvalidDataException {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidDataException(fieldName + " cannot be empty.");
        }
    }

    public static void requireValidEmail(String email) throws InvalidDataException {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidDataException("Invalid email address: " + email);
        }
    }

    public static void requirePositive(double value, String fieldName) throws InvalidDataException {
        if (value <= 0) {
            throw new InvalidDataException(fieldName + " must be positive.");
        }
    }

    public static void requireNonNegative(double value, String fieldName) throws InvalidDataException {
        if (value < 0) {
            throw new InvalidDataException(fieldName + " cannot be negative.");
        }
    }
}
