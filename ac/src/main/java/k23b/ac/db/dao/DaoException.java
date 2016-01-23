package k23b.ac.db.dao;

/**
 * Represents a problem encountered while accessing the underlying data store.
 */
@SuppressWarnings("serial")
public class DaoException extends Exception {

    public DaoException() {
    }

    public DaoException(String message) {
        super(message);
    }
}
