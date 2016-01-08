package k23b.am.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Data access layer for job objects.
 */
public class JobDao {

    private static final Logger log = Logger.getLogger(JobDao.class);

    /**
     * Creates a new job using the data provided.
     * 
     * @param agentId id of the agent for which the job is being created.
     * @param adminId id of the admin that is creating the job.
     * @param timeAssigned time of job assignment.
     * @param params job parameters.
     * @param periodic job's periodic property.
     * @param period job's period, if periodic.
     * @return the newly created job's id.
     * @throws DaoException if the newly created job's id could not be retrieved or a data access error occurs.
     */
    public static long create(long agentId, long adminId, Date timeAssigned, String params, boolean periodic, int period) throws DaoException {

        String sql = new StringBuilder()
                .append("insert into amdb_job ")
                .append("(AGENT_ID, ADMIN_ID, TIME_ASSIGNED, PARAMS, PERIODIC, PERIOD) ")
                .append("values ")
                .append("(?, ?, ?, ?, ?, ?)")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + agentId);
            ss.setLong(1, agentId);

            log.debug("Setting parameter 2 to: " + adminId);
            ss.setLong(2, adminId);

            log.debug("Setting parameter 3 to: " + timeAssigned);
            ss.setTimestamp(3, new Timestamp(timeAssigned.getTime()));

            log.debug("Setting parameter 4 to: " + params);
            ss.setString(4, params);

            log.debug("Setting parameter 5 to: " + periodic);
            ss.setBoolean(5, periodic);

            log.debug("Setting parameter 6 to: " + period);
            ss.setInt(6, period);

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "inserted.");

            ResultSet rs = ss.getGeneratedKeys();

            if (!rs.next())
                throw new DaoException("Could not retrieve id for created job.");

            long id = rs.getLong(1);
            log.debug("Id returned: " + id);

            return id;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while creating job for agent with id: " + agentId);
        }
    }

    /**
     * Retrieves an existing job with specified id.
     * 
     * @param jobId the job's id.
     * @return an object representing the job found, or null if a job with specified id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static JobDao findById(long jobId) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_job ")
                .append("where ")
                .append("JOB_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + jobId);
            ss.setLong(1, jobId);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            if (rs.next()) {

                long id = rs.getLong("JOB_ID");
                long agentId = rs.getLong("AGENT_ID");
                long adminId = rs.getLong("ADMIN_ID");
                Date timeAssigned = rs.getTimestamp("TIME_ASSIGNED");
                Date timeSent = rs.getTimestamp("TIME_SENT");
                String params = rs.getString("PARAMS");
                boolean periodic = rs.getBoolean("PERIODIC");
                int period = rs.getInt("PERIOD");
                Date timeTerminated = rs.getTimestamp("TIME_STOPPED");

                log.debug("1 row selected.");
                return new JobDao(id, agentId, adminId, timeAssigned, timeSent, params, periodic, period, timeTerminated);

            } else {

                log.debug("0 rows selected.");
                return null;
            }

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding AdminDao by id: " + jobId);
        }
    }

    /**
     * Retrieves all jobs that have been assigned to a specific agent.
     * 
     * @param agentId the agent's id.
     * @return a set of objects, each representing a job.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<JobDao> findAllWithAgentId(long agentId) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_job ")
                .append("where ")
                .append("AGENT_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + agentId);
            ss.setLong(1, agentId);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            Set<JobDao> jobs = new HashSet<JobDao>();

            int rows = 0;

            while (rs.next()) {

                rows++;
                long jobId = rs.getLong("JOB_ID");
                long id = rs.getLong("AGENT_ID");
                long adminId = rs.getLong("ADMIN_ID");
                Date timeAssigned = rs.getTimestamp("TIME_ASSIGNED");
                Date timeSent = rs.getTimestamp("TIME_SENT");
                String params = rs.getString("PARAMS");
                boolean periodic = rs.getBoolean("PERIODIC");
                int period = rs.getInt("PERIOD");
                Date timeTerminated = rs.getTimestamp("TIME_STOPPED");

                jobs.add(new JobDao(jobId, id, adminId, timeAssigned, timeSent, params, periodic, period, timeTerminated));
            }

            log.debug(rows + (rows == 1 ? " row " : " rows ") + "selected.");
            return jobs;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while fetching agents with agentId: " + agentId);
        }
    }

    /**
     * Retrieves all sent or not sent jobs that have been assigned to a specific agent.
     * 
     * @param agentId the agent's id.
     * @param sent the jobs' status, sent or not sent.
     * @return a set of objects, each representing a job.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<JobDao> findAllWithAgentId(long agentId, boolean sent) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_job ")
                .append("where ")
                .append("AGENT_ID = ? and ")
                .append("TIME_SENT IS " + (!sent ? "NULL" : "NOT NULL"))
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + agentId);
            ss.setLong(1, agentId);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            Set<JobDao> jobs = new HashSet<JobDao>();

            int rows = 0;

            while (rs.next()) {

                rows++;
                long jobId = rs.getLong("JOB_ID");
                long id = rs.getLong("AGENT_ID");
                long adminId = rs.getLong("ADMIN_ID");
                Date timeAssigned = rs.getTimestamp("TIME_ASSIGNED");
                Date timeSent = rs.getTimestamp("TIME_SENT");
                String params = rs.getString("PARAMS");
                boolean periodic = rs.getBoolean("PERIODIC");
                int period = rs.getInt("PERIOD");
                Date timeTerminated = rs.getTimestamp("TIME_STOPPED");

                jobs.add(new JobDao(jobId, id, adminId, timeAssigned, timeSent, params, periodic, period, timeTerminated));
            }

            log.debug(rows + (rows == 1 ? " row " : " rows ") + "selected.");
            return jobs;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while fetching agents with agentId: " + agentId);
        }
    }

    /**
     * Retrieves all jobs that have been assigned by a specific admin.
     * 
     * @param adminId the admin's id.
     * @return a set of objects, each representing a job.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<JobDao> findAllWithAdminId(long adminId) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_job ")
                .append("where ")
                .append("ADMIN_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + adminId);
            ss.setLong(1, adminId);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            Set<JobDao> jobs = new HashSet<JobDao>();

            int rows = 0;

            while (rs.next()) {

                rows++;
                long jobId = rs.getLong("JOB_ID");
                long agentId = rs.getLong("AGENT_ID");
                long id = rs.getLong("ADMIN_ID");
                Date timeAssigned = rs.getTimestamp("TIME_ASSIGNED");
                Date timeSent = rs.getTimestamp("TIME_SENT");
                String params = rs.getString("PARAMS");
                boolean periodic = rs.getBoolean("PERIODIC");
                int period = rs.getInt("PERIOD");
                Date timeTerminated = rs.getTimestamp("TIME_STOPPED");

                jobs.add(new JobDao(jobId, agentId, id, timeAssigned, timeSent, params, periodic, period, timeTerminated));
            }

            log.debug(rows + (rows == 1 ? " row " : " rows ") + "selected.");
            return jobs;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while fetching agents with adminId: " + adminId);
        }
    }

    /**
     * Sets a job's sent time to a specific value.
     * 
     * @param jobId the job's id.
     * @param timeSent the job's sent time.
     * @throws DaoException if the job's sent time could not be set to the specified value or a data access error occurs.
     */
    public static void setTimeSent(long jobId, Date timeSent) throws DaoException {

        String sql = new StringBuilder()
                .append("update amdb_job set ")
                .append("TIME_SENT = ? ")
                .append("where ")
                .append("JOB_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + timeSent);
            ss.setTimestamp(1, new Timestamp(timeSent.getTime()));

            log.debug("Setting parameter 2 to: " + jobId);
            ss.setLong(2, jobId);

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "updated.");

            if (rows != 1)
                throw new DaoException("Error while setting time sent to " + timeSent + " on job with id: " + jobId);

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while setting time sent to " + timeSent + " on job with id: " + jobId);
        }
    }

    /**
     * Sets a job's stop time to a specific value.
     * 
     * @param jobId the job's id.
     * @param timeStopped the job's stop time.
     * @throws DaoException if the job's stop time could not be set to the specified value or a data access error occurs.
     */
    public static void setTimeStopped(long jobId, Date timeStopped) throws DaoException {

        String sql = new StringBuilder()
                .append("update amdb_job set ")
                .append("TIME_STOPPED = ? ")
                .append("where ")
                .append("JOB_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + timeStopped);
            ss.setTimestamp(1, new Timestamp(timeStopped.getTime()));

            log.debug("Setting parameter 2 to: " + jobId);
            ss.setLong(2, jobId);

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "updated.");

            if (rows != 1)
                throw new DaoException("Error while setting time stopped to " + timeStopped + " on job with id: " + jobId);

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while setting time stopped to " + timeStopped + " on job with id: " + jobId);
        }
    }

    private long jobId;
    private long agentId;
    private long adminId;
    private Date timeAssigned;
    private Date timeSent;
    private String params;
    private boolean periodic;
    private int period;
    private Date timeStopped;

    public long getJobId() {
        return jobId;
    }

    public long getAgentId() {
        return agentId;
    }

    public long getAdminId() {
        return adminId;
    }

    public Date getTimeAssigned() {
        return timeAssigned;
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

    public boolean getPeriodic() {
        return periodic;
    }

    public int getPeriod() {
        return period;
    }

    public Date getTimeStopped() {
        return timeStopped;
    }

    public void setTimeStopped(Date timeStopped) {
        this.timeStopped = timeStopped;
    }

    public boolean getSent() {
        return timeSent != null;
    }

    private JobDao(long jobId, long agentId, long adminId, Date timeAssigned, Date timeSent, String params, boolean periodic, int period, Date timeStopped) {
        super();
        this.jobId = jobId;
        this.agentId = agentId;
        this.adminId = adminId;
        this.timeAssigned = timeAssigned;
        this.timeSent = timeSent;
        this.params = params;
        this.periodic = periodic;
        this.period = period;
        this.timeStopped = timeStopped;
    }
}
