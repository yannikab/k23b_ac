package k23b.ac.rest;

import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "agent")
public class Agent {

    @Element(required = true)
    private long agentId;

    @Element(required = true)
    private String requestHash;

    @Element(required = true)
    private String adminUsername;

    @Element(required = true)
    private Date timeAccepted;

    @Element(required = false)
    private Date timeJobRequest;

    @Element(required = false)
    private Date timeTerminated;

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

    public String getAdminUsername() {
        return adminUsername;
    }

    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
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

    @Override
    public String toString() {
        return "Agent [agentId=" + agentId + ", requestHash=" + requestHash + ", adminUsername=" + adminUsername + ", timeAccepted=" + timeAccepted + ", timeJobRequest=" + timeJobRequest + ", timeTerminated=" + timeTerminated + "]";
    }
}
