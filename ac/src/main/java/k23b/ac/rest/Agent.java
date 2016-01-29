package k23b.ac.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.annotation.SuppressLint;
import k23b.ac.rest.status.AgentStatus;

@Root(name = "agent")
public class Agent implements Comparable<Agent> {

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

    @Element(required = true)
    private AgentStatus agentStatus;

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

    public String getShortRequestHash() {

        if (requestHash == null)
            return "";

        final int maxLength = 7;

        return requestHash.length() < maxLength ? requestHash : requestHash.substring(0, maxLength);
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

    public AgentStatus getAgentStatus() {
        return agentStatus;
    }

    public void setAgentStatus(AgentStatus agentStatus) {
        this.agentStatus = agentStatus;
    }

    @SuppressLint("SimpleDateFormat")
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public String getFormattedTimeAccepted() {

        if (timeAccepted == null)
            return "-";

        return dateFormat.format(timeAccepted);
    }

    public String getFormattedTimeJobRequest() {

        if (timeJobRequest == null)
            return "-";

        return dateFormat.format(timeJobRequest);
    }

    public String getFormattedTimeTerminated() {

        if (timeTerminated == null)
            return "-";

        return dateFormat.format(timeTerminated);
    }

    @Override
    public String toString() {
        return "Agent [agentId=" + agentId + ", requestHash=" + requestHash + ", adminUsername=" + adminUsername + ", timeAccepted=" + timeAccepted + ", timeJobRequest=" + timeJobRequest + ", timeTerminated=" + timeTerminated + "]";
    }

    @Override
    public int compareTo(Agent that) {

        if (this.equals(that))
            return 0;

        if (this.agentId > that.agentId)
            return -1;
        else if (this.agentId < that.agentId)
            return 1;
        else
            return 0;
    }
}
