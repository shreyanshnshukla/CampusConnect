package campusconnect.exception;

/** Thrown when a registration is attempted after an event has reached its capacity. */
public class CapacityExceededException extends Exception {
    public CapacityExceededException(String message) { super(message); }
}
