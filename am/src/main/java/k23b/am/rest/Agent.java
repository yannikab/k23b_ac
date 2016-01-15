package k23b.am.rest;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "agent")
@XmlAccessorType(XmlAccessType.FIELD)
public class Agent {

    @XmlElement(required = true)
    private long agentId;

    @XmlElement(required = true)
    private String requestHash;

    @XmlElement(required = true)
    private String adminUsername;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = true)
    private Date timeAccepted;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = false)
    private Date timeJobRequest;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = false)
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
