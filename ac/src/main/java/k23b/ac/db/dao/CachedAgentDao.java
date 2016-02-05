package k23b.ac.db.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CachedAgentDao {

    // this method assumes that an agent does not exist with this id, and blindly creates it
    public static void create(long agentId, String agentHash, Date timeAccepted, Date timeJobRequest, Date timeTerminated, CachedAgentStatus agentStatus) throws DaoException {
        return;
    }

    // this method assumes that an agent exists with this id, and blindly updates it
    public static void update(long agentId, String agentHash, Date timeAccepted, Date timeJobRequest, Date timeTerminated, CachedAgentStatus agentStatus) throws DaoException {
        return;
    }

    public static boolean exists(long agentId) throws DaoException {
        return false;
    }

    public static Set<CachedAgentDao> findAll() throws DaoException {
        return new HashSet<CachedAgentDao>();
    }

    private long agentId;
    private String agentHash;
    private Date timeAccepted;
    private Date timeJobRequest;
    private Date timeTerminated;
    private CachedAgentStatus agentStatus;

    public long getAgentId() {
        return agentId;
    }

    public String getAgentHash() {
        return agentHash;
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

    public CachedAgentStatus getAgentStatus() {
        return agentStatus;
    }

    public void setAgentStatus(CachedAgentStatus agentStatus) {
        this.agentStatus = agentStatus;
    }

    public CachedAgentDao(long agentId, String agentHash, Date timeAccepted, Date timeJobRequest, Date timeTerminated, CachedAgentStatus agentStatus) {
        super();
        this.agentId = agentId;
        this.agentHash = agentHash;
        this.timeAccepted = timeAccepted;
        this.timeJobRequest = timeJobRequest;
        this.timeTerminated = timeTerminated;
        this.agentStatus = agentStatus;
    }
}
