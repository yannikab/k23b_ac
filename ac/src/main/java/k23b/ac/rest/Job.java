package k23b.ac.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.annotation.SuppressLint;

@Root(name = "job")
public class Job implements Comparable<Job> {

    @Element(required = true)
    private long jobId;

    @Element(required = true)
    private long agentId;

    @Element(required = true)
    private long adminId;

    @Element(required = true)
    private Date timeAssigned;

    @Element(required = false)
    private Date timeSent;

    @Element(required = true)
    private String params;

    @Element(required = true)
    private boolean periodic;

    @Element(required = false)
    private int period;

    @Element(required = false)
    private Date timeStopped;

    public Job() {
        super();
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    public Date getTimeAssigned() {
        return timeAssigned;
    }

    public void setTimeAssigned(Date timeAssigned) {
        this.timeAssigned = timeAssigned;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(Date timeSent) {
        this.timeSent = timeSent;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public boolean getPeriodic() {
        return periodic;
    }

    public void setPeriodic(boolean periodic) {
        this.periodic = periodic;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Date getTimeStopped() {
        return timeStopped;
    }

    public void setTimeStopped(Date timeStopped) {
        this.timeStopped = timeStopped;
    }

    public JobStatus getStatus() {

        if (this.getPeriodic() && this.getTimeStopped() != null)
            return JobStatus.STOPPED;
        else if (this.getTimeSent() != null)
            return JobStatus.SENT;
        else
            return JobStatus.ASSIGNED;
    }

    @SuppressLint("SimpleDateFormat")
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public String getFormattedTimeAssigned() {

        if (timeAssigned == null)
            return String.valueOf(timeAssigned);

        return dateFormat.format(timeAssigned);
    }

    public String getFormattedTimeSent() {

        if (timeSent == null)
            return String.valueOf(timeSent);

        return dateFormat.format(timeSent);
    }

    public String getFormattedTimeStopped() {

        if (timeStopped == null)
            return String.valueOf(timeStopped);

        return dateFormat.format(timeStopped);
    }

    @Override
    public String toString() {
        return "Job [jobId=" + jobId + ", agentId=" + agentId + ", adminId=" + adminId + ", timeAssigned=" + timeAssigned + ", timeSent=" + timeSent + ", params=" + params + ", periodic=" + periodic + ", period=" + period + ", timeStopped=" + timeStopped + "]";
    }

    @Override
    public int compareTo(Job that) {

        if (this.equals(that))
            return 0;

        if (this.getJobId() > that.getJobId())
            return 1;
        else if (this.getJobId() < that.getJobId())
            return -1;
        else
            return 0;
    }
}