package campusconnect.exception;

/** Thrown when a participant tries to register twice for the same active event. */
public class DuplicateRegistrationException extends Exception {
    public DuplicateRegistrationException(String message) { super(message); }
}
