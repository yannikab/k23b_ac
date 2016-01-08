package k23b.am.model;

import java.time.Instant;
import java.util.Date;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Agent {

    private LongProperty agentIdProperty;
    private StringProperty requestHashProperty;
    private StringProperty adminUsernameProperty;
    private ObjectProperty<Date> timeAcceptedProperty;
    private ObjectProperty<Date> timeJobRequestProperty;
    private ObjectProperty<Date> timeTerminatedProperty;
    private ObjectProperty<AgentStatus> agentStatusProperty;

    public LongProperty getAgentIdProperty() {
        return agentIdProperty;
    }

    public StringProperty getRequestHashProperty() {
        return requestHashProperty;
    }

    public StringProperty getAdminUsernameProperty() {
        return adminUsernameProperty;
    }

    public ObjectProperty<Date> getTimeAcceptedProperty() {
        return timeAcceptedProperty;
    }

    public ObjectProperty<Date> getTimeJobRequestProperty() {
        return timeJobRequestProperty;
    }

    public ObjectProperty<Date> getTimeTerminatedProperty() {
        return timeTerminatedProperty;
    }

    public ObjectProperty<AgentStatus> getAgentStatusProperty() {

        Date timeActive = getTimeJobRequestProperty().getValue();

        if (timeActive == null) {
            agentStatusProperty.setValue(AgentStatus.OFFLINE);
            return agentStatusProperty;
        }

        Date now = Date.from(Instant.now());

        long seconds = (now.getTime() - timeActive.getTime()) / 1000;

        if (seconds > 3 * jobRequestInterval || seconds < 0)
            agentStatusProperty.setValue(AgentStatus.OFFLINE);
        else
            agentStatusProperty.setValue(AgentStatus.ONLINE);

        return agentStatusProperty;
    }

    int jobRequestInterval;

    public Agent(long agentId, String requestHash, String adminUsername, Date timeAccepted, Date timeJobRequest, Date timeTerminated, int jobRequestInterval) {
        super();
        this.agentIdProperty = new SimpleLongProperty(agentId);
        this.requestHashProperty = new SimpleStringProperty(requestHash);
        this.adminUsernameProperty = new SimpleStringProperty(adminUsername);
        this.timeAcceptedProperty = new SimpleObjectProperty<Date>(timeAccepted);
        this.timeJobRequestProperty = new SimpleObjectProperty<Date>(timeJobRequest);
        this.timeTerminatedProperty = new SimpleObjectProperty<Date>(timeTerminated);
        this.agentStatusProperty = new SimpleObjectProperty<AgentStatus>();
        this.jobRequestInterval = jobRequestInterval;
    }

    public long getAgentId() {
        return this.agentIdProperty.get();
    }

    public String getRequestHash() {
        return this.requestHashProperty.get();
    }

    public String getAdminUserName() {
        return this.adminUsernameProperty.get();
    }

    public Date getTimeAccepted() {
        return this.timeAcceptedProperty.get();
    }

    public Date getTimeJobRequest() {
        return this.timeJobRequestProperty.get();
    }

    public void setTimeJobRequest(Date timeJobRequest) {
        this.timeJobRequestProperty.set(timeJobRequest);
    }

    public Date getTimeTerminated() {
        return this.timeTerminatedProperty.get();
    }

    public void setTimeTerminated(Date timeTerminated) {
        this.timeTerminatedProperty.set(timeTerminated);
    }

    public AgentStatus getAgentStatus() {
        return this.getAgentStatusProperty().get();
    }
}
