package k23b.ac.rest.status;

public enum JobStatus {
    ASSIGNED, SENT, STOPPED, ALL;

    @Override
    public String toString() {
        switch (this) {
        case ASSIGNED:
            return "Assigned";
        case SENT:
            return "Sent";
        case STOPPED:
            return "Stopped";
        case ALL:
            return "All";
        default:
            return "";
        }
    }
}
