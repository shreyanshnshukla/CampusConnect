package campusconnect.exception;

/** Thrown when an operation references a participant ID that does not exist. */
public class ParticipantNotFoundException extends Exception {
    public ParticipantNotFoundException(String message) { super(message); }
}
