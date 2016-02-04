package k23b.am.rest;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import k23b.am.model.AgentStatus;

@XmlRootElement(name = "agent")
@XmlAccessorType(XmlAccessType.FIELD)
public class Agent {

    @XmlElement(required = true)
    protected long agentId;

    @XmlElement(required = true)
    protected String requestHash;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = true)
    protected Date timeAccepted;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = false)
    protected Date timeJobRequest;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = false)
    protected Date timeTerminated;

    @XmlElement(required = true)
    protected AgentStatus agentStatus;

    public Agent() {
        super();
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public void setRequestHash(String requestHash) {
        this.requestHash = requestHash;
    }

    public Date getTimeAccepted() {
        return timeAccepted;
    }

    public void setTimeAccepted(Date timeAccepted) {
        this.timeAccepted = timeAccepted;
    }

    public Date getTimeJobRequest() {
        return timeJobRequest;
    }

    public void setTimeJobRequest(Date timeJobRequest) {
        this.timeJobRequest = timeJobRequest;
    }

    public Date getTimeTerminated() {
        return timeTerminated;
    }

    public void setTimeTerminated(Date timeTerminated) {
        this.timeTerminated = timeTerminated;
    }

    protected AgentStatus getStatus(int jobRequestInterval) {

        Date timeActive = getTimeJobRequest();

        if (timeActive == null)
            return AgentStatus.OFFLINE;

        Date now = new Date(System.currentTimeMillis());

        long seconds = (now.getTime() - timeActive.getTime()) / 1000;

        if (seconds > 3 * jobRequestInterval || seconds < 0)
            return AgentStatus.OFFLINE;
        else
            return AgentStatus.ONLINE;
    }

    @Override
    public String toString() {
        return "Agent [agentId=" + agentId + ", requestHash=" + requestHash + ", timeAccepted=" + timeAccepted + ", timeJobRequest=" + timeJobRequest + ", timeTerminated=" + timeTerminated + "]";
    }
}
