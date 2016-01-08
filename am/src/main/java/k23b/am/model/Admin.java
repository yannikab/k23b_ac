package k23b.am.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Admin {

    private LongProperty adminIdProperty;
    private StringProperty usernameProperty;
    private StringProperty passwordProperty;
    private BooleanProperty activeProperty;

    public LongProperty getAdminIdProperty() {
        return adminIdProperty;
    }

    public StringProperty getUsernameProperty() {
        return usernameProperty;
    }

    public StringProperty getPasswordProperty() {
        return passwordProperty;
    }

    public BooleanProperty getActiveProperty() {
        return activeProperty;
    }

    public Admin(long adminId, String username, String password, boolean active) {
        super();
        this.adminIdProperty = new SimpleLongProperty(adminId);
        this.usernameProperty = new SimpleStringProperty(username);
        this.passwordProperty = new SimpleStringProperty(password);
        this.activeProperty = new SimpleBooleanProperty(active);
    }

    public long getAdminId() {
        return this.adminIdProperty.get();
    }

    public void setAdminId(long adminId) {
        this.adminIdProperty.set(adminId);
    }

    public String getUsername() {
        return this.usernameProperty.get();
    }

    public void setUserName(String username) {
        this.usernameProperty.set(username);
    }

    public String getPassword() {
        return passwordProperty.get();
    }

    public void setPassword(String password) {
        this.passwordProperty.set(password);
    }

    public boolean getActive() {
        return activeProperty.get();
    }

    public void setActive(boolean active) {
        this.activeProperty.set(active);
    }
}
