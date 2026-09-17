package campusconnect.exception;

/** Thrown when an operation references a registration ID that does not exist. */
public class RegistrationNotFoundException extends Exception {
    public RegistrationNotFoundException(String message) { super(message); }
}
