package k23b.am.model;

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
