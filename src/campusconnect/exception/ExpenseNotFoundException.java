package campusconnect.exception;

/** Thrown when an operation references an expense ID that does not exist. */
public class ExpenseNotFoundException extends Exception {
    public ExpenseNotFoundException(String message) { super(message); }
}
