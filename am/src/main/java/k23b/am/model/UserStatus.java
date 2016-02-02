package k23b.am.model;

public enum UserStatus {
    INACTIVE, ACTIVE, ALL;

    @Override
    public String toString() {
        switch (this) {
        case INACTIVE:
            return "Inactive";
        case ACTIVE:
            return "Active";
        case ALL:
            return "All";
        default:
            return "";
        }
    }
}
