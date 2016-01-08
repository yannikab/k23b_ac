package k23b.am.dao;

/**
 * Represents the possible values that a request's status can have.
 */
public enum RequestStatus {
    PENDING, ACCEPTED, REJECTED, ALL;

    @Override
    public String toString() {
        switch (this) {
        case PENDING:
            return "Pending";
        case ACCEPTED:
            return "Accepted";
        case REJECTED:
            return "Rejected";
        case ALL:
            return "All";
        default:
            return "";
        }
    }
}
