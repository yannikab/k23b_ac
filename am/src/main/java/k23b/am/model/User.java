package k23b.am.model;

import java.time.Instant;
import java.util.Date;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {

    private LongProperty userIdProperty;
    private StringProperty usernameProperty;
    private StringProperty adminUsernameProperty;
    private ObjectProperty<Date> timeRegisteredProperty;
    private ObjectProperty<Date> timeAcceptedProperty;
    private ObjectProperty<Date> timeActiveProperty;
    private ObjectProperty<UserStatus> userStatusProperty;

    public LongProperty getUserIdProperty() {
        return userIdProperty;
    }

    public StringProperty getUsernameProperty() {
        return usernameProperty;
    }
    
    public StringProperty getAdminUsernameProperty() {
        return adminUsernameProperty;
    }

    public ObjectProperty<Date> getTimeRegisteredProperty() {
        return timeRegisteredProperty;
    }

    public ObjectProperty<Date> getTimeAcceptedProperty() {
        return timeAcceptedProperty;
    }

    public ObjectProperty<Date> getTimeActiveProperty() {
        return timeActiveProperty;
    }

    public ObjectProperty<UserStatus> getUserStatusProperty() {

        Date timeActive = getTimeActiveProperty().getValue();

        if (timeActive == null) {
            userStatusProperty.setValue(UserStatus.INACTIVE);
            return userStatusProperty;
        }

        Date now = Date.from(Instant.now());

        long seconds = (now.getTime() - timeActive.getTime()) / 1000;

        if (seconds >= 0 && seconds <= userSessionMinutes * 60)
            userStatusProperty.setValue(UserStatus.ACTIVE);
        else
            userStatusProperty.setValue(UserStatus.INACTIVE);

        return userStatusProperty;
    }

    int userSessionMinutes;

    public User(long userId, String username, String adminUsername, Date timeRegistered, Date timeAccepted, Date timeActive, int userSessionMinutes) {
        super();
        this.userIdProperty = new SimpleLongProperty(userId);
        this.usernameProperty = new SimpleStringProperty(username);
        this.adminUsernameProperty = new SimpleStringProperty(adminUsername);
        this.timeRegisteredProperty = new SimpleObjectProperty<Date>(timeRegistered);
        this.timeAcceptedProperty = new SimpleObjectProperty<Date>(timeAccepted);
        this.timeActiveProperty = new SimpleObjectProperty<Date>(timeActive);
        this.userStatusProperty = new SimpleObjectProperty<UserStatus>();
        this.userSessionMinutes = userSessionMinutes;
    }

    public long getUserId() {
        return this.userIdProperty.get();
    }

    public String getUserName() {
        return this.usernameProperty.get();
    }
    
    public String getAdminUserName() {
        return this.adminUsernameProperty.get();
    }
    
    public void setAdminUserName(String adminUsername) {
        this.adminUsernameProperty.set(adminUsername);
    }

    public Date getTimeRegistered() {
        return this.timeRegisteredProperty.get();
    }

    public Date getTimeAccepted() {
        return this.timeAcceptedProperty.get();
    }

    public void setTimeAccepted(Date timeAccepted) {
        this.timeAcceptedProperty.set(timeAccepted);
    }

    public Date getTimeActive() {
        return this.timeActiveProperty.get();
    }

    public void setTimeActive(Date timeActive) {
        this.timeActiveProperty.set(timeActive);
    }

    public UserStatus getUserStatus() {
        return this.getUserStatusProperty().get();
    }
}
