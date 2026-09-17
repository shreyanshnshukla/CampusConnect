package campusconnect.exception;

/** Thrown when an operation references a sponsor ID that does not exist. */
public class SponsorNotFoundException extends Exception {
    public SponsorNotFoundException(String message) { super(message); }
}
