package k23b.ac.rest.status;

/**
 * An enumeration of the possible Agent Status values
 */
public enum AgentStatus {
    OFFLINE, ONLINE, ALL;

    @Override
    public String toString() {
        switch (this) {
        case OFFLINE:
            return "Offline";
        case ONLINE:
            return "Online";
        case ALL:
            return "All";
        default:
            return "";
        }
    }
}
