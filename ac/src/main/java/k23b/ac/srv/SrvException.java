package k23b.ac.srv;

/**
 * Represents an attempt to manipulate data in a way that violates business logic, or an underlying data access error.
 */
@SuppressWarnings("serial")
public class SrvException extends Exception {

    public SrvException() {
    }

    public SrvException(String message) {
        super(message);
    }
}
