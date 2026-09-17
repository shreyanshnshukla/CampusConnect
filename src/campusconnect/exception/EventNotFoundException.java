package campusconnect.exception;

/** Thrown when an operation references an event ID that does not exist. */
public class EventNotFoundException extends Exception {
    public EventNotFoundException(String message) { super(message); }
}
