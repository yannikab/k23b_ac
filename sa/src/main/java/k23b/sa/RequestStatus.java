package k23b.sa;

/**
 * 	An enumeration of the Request Status of this SA. 
 */
public enum RequestStatus {
    PENDING, ACCEPTED, REJECTED, UNKNOWN, INVALID;

    @Override
    public String toString() {
        switch (this) {
        case PENDING:
            return "Pending";
        case ACCEPTED:
            return "Accepted";
        case REJECTED:
            return "Rejected";
        case UNKNOWN:
            return "Unknown";
        case INVALID:
            return "Invalid";
        default:
            return "";
        }
    }
}
