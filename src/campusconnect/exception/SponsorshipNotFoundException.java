package campusconnect.exception;

/** Thrown when an operation references a sponsorship ID that does not exist. */
public class SponsorshipNotFoundException extends Exception {
    public SponsorshipNotFoundException(String message) { super(message); }
}
