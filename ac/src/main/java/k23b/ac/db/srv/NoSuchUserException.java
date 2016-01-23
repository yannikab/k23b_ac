package k23b.ac.db.srv;

@SuppressWarnings("serial")
public class NoSuchUserException extends SrvException {

    public NoSuchUserException() {
    }

    public NoSuchUserException(String message) {
        super(message);
    }
}
