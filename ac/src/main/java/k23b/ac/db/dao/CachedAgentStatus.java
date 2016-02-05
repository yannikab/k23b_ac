package k23b.ac.db.dao;

public enum CachedAgentStatus {
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
