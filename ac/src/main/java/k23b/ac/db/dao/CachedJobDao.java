package k23b.ac.db.dao;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class CachedJobDao {

    // this method assumes that a job does not exist with this id, and blindly creates it
    public static void create(long jobId, long agentId, Date timeAssigned, Date timeSent, String parameters, boolean periodic, int period, CachedJobStatus status) throws DaoException {
        return;
    }

    // this method assumes that a job exists with this id, and blindly updates it
    public static void update(long jobId, long agentId, Date timeAssigned, Date timeSent, String parameters, boolean periodic, int period, CachedJobStatus status) throws DaoException {
        return;
    }

    public static boolean exists(long jobId) throws DaoException {
        return false;
    }

    public static Set<CachedJobDao> findAllWithAgentId(Object agentId) throws DaoException {
        return new HashSet<CachedJobDao>();
    }

    private long jobId;
    private long agentId;
    private Date timeAssigned;
    private Date timeSent;
    private String parameters;
    private boolean periodic;
    private int period;
    private CachedJobStatus status;

    public long getJobId() {
        return jobId;
    }

    public long getAgentId() {
        return agentId;
    }

    public Date getTimeAssigned() {
        return timeAssigned;
    }

    public Date getTimeSent() {
        return timeSent;
    }

    public String getParameters() {
        return parameters;
    }

    public boolean getPeriodic() {
        return periodic;
    }

    public int getPeriod() {
        return period;
    }

    public CachedJobStatus getStatus() {
        return status;
    }

    public CachedJobDao(long jobId, long agentId, Date timeAssigned, Date timeSent, String parameters, boolean periodic, int period, CachedJobStatus status) {
        super();
        this.jobId = jobId;
        this.agentId = agentId;
        this.timeAssigned = timeAssigned;
        this.timeSent = timeSent;
        this.parameters = parameters;
        this.periodic = periodic;
        this.period = period;
        this.status = status;
    }
}
