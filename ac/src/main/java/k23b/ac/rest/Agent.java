package k23b.ac.rest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import android.annotation.SuppressLint;

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

    @SuppressLint("SimpleDateFormat")
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public String getFormattedTimeAccepted() {

        if (timeAccepted == null)
            return String.valueOf(timeAccepted);

        return dateFormat.format(timeAccepted);
    }

    public String getFormattedTimeJobRequest() {

        if (timeJobRequest == null)
            return String.valueOf(timeJobRequest);

        return dateFormat.format(timeJobRequest);
    }

    public String getFormattedTimeTerminated() {

        if (timeTerminated == null)
            return String.valueOf(timeTerminated);

        return dateFormat.format(timeTerminated);
    }

    public AgentStatus getStatus(int jobRequestInterval) {

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
        return "Agent [agentId=" + agentId + ", requestHash=" + requestHash + ", adminUsername=" + adminUsername + ", timeAccepted=" + timeAccepted + ", timeJobRequest=" + timeJobRequest + ", timeTerminated=" + timeTerminated + "]";
    }

    @Override
    public int compareTo(Agent that) {

        if (this.equals(that))
            return 0;

        if (this.getAgentId() > that.getAgentId())
            return 1;
        else if (this.getAgentId() < that.getAgentId())
            return -1;
        else
            return 0;
    }
}
