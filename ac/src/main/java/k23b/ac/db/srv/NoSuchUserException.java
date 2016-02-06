package k23b.ac.db.srv;

/**
 * Represents a specific data access error where a User does not exist 
 */
@SuppressWarnings("serial")
public class NoSuchUserException extends SrvException {

    public NoSuchUserException() {
    }

    public NoSuchUserException(String message) {
        super(message);
    }
}
