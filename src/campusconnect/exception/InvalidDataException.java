package campusconnect.exception;

/** Thrown when user-supplied input fails validation (empty fields, negative amounts, bad email, etc.). */
public class InvalidDataException extends Exception {
    public InvalidDataException(String message) { super(message); }
}
