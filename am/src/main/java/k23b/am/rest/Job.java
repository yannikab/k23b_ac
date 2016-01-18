package k23b.am.rest;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Serves the purpose of sending Job information.
 */

@XmlRootElement(name = "job")
@XmlAccessorType(XmlAccessType.FIELD)
public class Job {

    @XmlElement(required = true)
    private long jobId;

    @XmlElement(required = true)
    private long agentId;

    @XmlElement(required = true)
    private long adminId;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = true)
    private Date timeAssigned;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = false)
    private Date timeSent;

    @XmlElement(required = true)
    private String params;

    @XmlElement(required = true)
    private boolean periodic;

    @XmlElement(required = false)
    private int period;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = false)
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

    @Override
    public String toString() {
        return "Job [jobId=" + jobId + ", agentId=" + agentId + ", adminId=" + adminId + ", timeAssigned=" + timeAssigned + ", timeSent=" + timeSent + ", params=" + params + ", periodic=" + periodic + ", period=" + period + ", timeStopped=" + timeStopped + "]";
    }
}
