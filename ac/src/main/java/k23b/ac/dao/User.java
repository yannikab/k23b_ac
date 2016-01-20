package k23b.ac.dao;

public class User {

    private String username;
    private String password;
    // private boolean active;

    public User(String username, String password, boolean active) {
        this.username = username;
        this.password = password;
        // this.active = active;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // public boolean isActive() {
    // return active;
    // }

    @Override
    public String toString() {
        return "User [username=" + username + ", password=" + password + ", active=" + "]";
    }
}
