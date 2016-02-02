package k23b.am.rest;

import java.util.Date;
import java.util.StringTokenizer;

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
    protected long jobId;

    @XmlElement(required = true)
    protected long agentId;

    @XmlElement(required = true)
    protected long adminId;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = true)
    protected Date timeAssigned;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = false)
    protected Date timeSent;

    @XmlElement(required = true)
    protected String params;

    @XmlElement(required = true)
    protected boolean periodic;

    @XmlElement(required = false)
    protected int period;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = false)
    protected Date timeStopped;

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

    public boolean isTerminating() {

        return params != null && params.equals("exit");
    }

    public boolean isPeriodicStop() {

        return params != null && params.startsWith("stop");
    }

    public long getPeriodicJobId() {

        if (!isPeriodicStop())
            return 0;

        StringTokenizer stk = new StringTokenizer(params, " ");

        if (stk.countTokens() != 2)
            return 0;

        stk.nextToken();

        try {
            
            return Long.valueOf(stk.nextToken());

        } catch (NumberFormatException e) {
            
            return 0;
        }
    }
}
