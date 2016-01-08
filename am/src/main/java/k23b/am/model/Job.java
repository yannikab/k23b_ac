package k23b.am.model;

import java.util.Date;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Job {

    private LongProperty jobIdProperty;
    private LongProperty agentIdProperty;
    private StringProperty adminUsernameProperty;
    private ObjectProperty<Date> timeAssignedProperty;
    private ObjectProperty<Date> timeSentProperty;
    private StringProperty paramsProperty;
    private BooleanProperty periodicProperty;
    private IntegerProperty periodProperty;
    private ObjectProperty<Date> timeStoppedProperty;
    public ObjectProperty<JobStatus> jobStatusProperty;

    public LongProperty getJobIdProperty() {
        return jobIdProperty;
    }

    public LongProperty getAgentIdProperty() {
        return agentIdProperty;
    }

    public StringProperty getAdminUsernameProperty() {
        return adminUsernameProperty;
    }

    public ObjectProperty<Date> getTimeAssignedProperty() {
        return timeAssignedProperty;
    }

    public ObjectProperty<Date> getTimeSentProperty() {
        return timeSentProperty;
    }

    public StringProperty getParamsProperty() {
        return paramsProperty;
    }

    public BooleanProperty getPeriodicProperty() {
        return periodicProperty;
    }

    public IntegerProperty getPeriodProperty() {
        return periodProperty;
    }

    public ObjectProperty<Date> getTimeStoppedProperty() {
        return timeStoppedProperty;
    }

    public ObjectProperty<JobStatus> getJobStatusProperty() {

        if (this.getPeriodic() && this.getTimeStopped() != null)
            jobStatusProperty.setValue(JobStatus.STOPPED);
        else if (this.getTimeSent() != null)
            jobStatusProperty.setValue(JobStatus.SENT);
        else
            jobStatusProperty.setValue(JobStatus.ASSIGNED);

        return jobStatusProperty;
    }

    public Job(long jobId, long agentId, String adminUsername, Date timeAssigned, Date timeSent, String params, boolean periodic, int period, Date timeStopped) {
        super();
        this.jobIdProperty = new SimpleLongProperty(jobId);
        this.agentIdProperty = new SimpleLongProperty(agentId);
        this.adminUsernameProperty = new SimpleStringProperty(adminUsername);
        this.timeAssignedProperty = new SimpleObjectProperty<Date>(timeAssigned);
        this.timeSentProperty = new SimpleObjectProperty<Date>(timeSent);
        this.paramsProperty = new SimpleStringProperty(params);
        this.periodicProperty = new SimpleBooleanProperty(periodic);
        this.periodProperty = new SimpleIntegerProperty(period);
        this.timeStoppedProperty = new SimpleObjectProperty<Date>(timeStopped);
        this.jobStatusProperty = new SimpleObjectProperty<JobStatus>();
    }

    public long getJobId() {
        return this.jobIdProperty.get();
    }

    public long getAgentId() {
        return this.agentIdProperty.get();
    }

    public String getAdminUserName() {
        return this.adminUsernameProperty.get();
    }

    public Date getTimeAssigned() {
        return this.timeAssignedProperty.get();
    }

    public Date getTimeSent() {
        return this.timeSentProperty.get();
    }

    public String getParams() {
        return this.paramsProperty.get();
    }

    public boolean getPeriodic() {
        return this.periodicProperty.get();
    }

    public void setPeriodic(boolean periodic) {
        this.periodicProperty.set(periodic);
    }

    public Date getTimeStopped() {
        return this.timeStoppedProperty.get();
    }

    public JobStatus getJobStatus() {
        return this.getJobStatusProperty().get();
    }

    public boolean isPeriodicStop() {
        return this.getParams() != null && this.getParams().matches("stop \\d*");
    }

    public boolean isAgentTermination() {
        return this.getParams() != null && this.getParams().equals("exit");
    }
}
