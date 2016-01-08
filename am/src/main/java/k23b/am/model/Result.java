package k23b.am.model;

import java.util.Date;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Result {

    private LongProperty resultIdProperty;
    private StringProperty requestHashProperty;
    private LongProperty jobIdProperty;
    private ObjectProperty<Date> timeReceivedProperty;
    private StringProperty outputProperty;

    public LongProperty getResultIdProperty() {
        return resultIdProperty;
    }

    public StringProperty getRequestHashProperty() {
        return requestHashProperty;
    }

    public LongProperty getJobIdProperty() {
        return jobIdProperty;
    }

    public ObjectProperty<Date> getTimeReceivedProperty() {
        return timeReceivedProperty;
    }

    public StringProperty getOutputProperty() {
        return outputProperty;
    }

    public Result(long resultId, String requestHash, long jobId, Date timeReceived, String output) {
        super();
        this.resultIdProperty = new SimpleLongProperty(resultId);
        this.requestHashProperty = new SimpleStringProperty(requestHash);
        this.jobIdProperty = new SimpleLongProperty(jobId);
        this.timeReceivedProperty = new SimpleObjectProperty<Date>(timeReceived);
        this.outputProperty = new SimpleStringProperty(output);
    }

    public long getResultId() {
        return this.resultIdProperty.get();
    }
    
    public String getRequestHash() {
        return this.requestHashProperty.get();
    }

    public long getJobId() {
        return this.jobIdProperty.get();
    }

    public Date getTimeReceived() {
        return this.timeReceivedProperty.get();
    }

    public void setTimeReceived(Date time) {
        this.timeReceivedProperty.set(time);
    }

    public String getOutput() {
        return this.outputProperty.get();
    }

    public void setOutput(String output) {
        this.outputProperty.set(output);
    }
}
