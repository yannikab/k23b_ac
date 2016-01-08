package k23b.am.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Data access layer for agent objects.
 */
public class AgentDao {

    private static final Logger log = Logger.getLogger(AgentDao.class);

    /**
     * Creates a new agent using the data provided.
     * 
     * @param requestId id of the request for which the agent is being created.
     * @param adminId id of the admin that is creating the agent.
     * @param timeAccepted the time of agent creation.
     * @return the newly created agent's id.
     * @throws DaoException if the newly created agent's id could not be retrieved or a data access error occurs.
     */
    public static long create(long requestId, long adminId, Date timeAccepted) throws DaoException {

        String sql = new StringBuilder()
                .append("insert into amdb_agent ")
                .append("(REQUEST_ID, ADMIN_ID, TIME_ACCEPTED) ")
                .append("values ")
                .append("(?, ?, ?)")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + requestId);
            ss.setLong(1, requestId);

            log.debug("Setting parameter 2 to: " + adminId);
            ss.setLong(2, adminId);

            log.debug("Setting parameter 3 to: " + timeAccepted);
            ss.setTimestamp(3, new Timestamp(timeAccepted.getTime()));

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "inserted.");

            ResultSet rs = ss.getGeneratedKeys();

            if (!rs.next())
                throw new DaoException("Could not retrieve id for created agent.");

            long id = rs.getLong(1);
            log.debug("Id returned: " + id);

            return id;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while creating agent with requestId: " + requestId);
        }
    }

    /**
     * Retrieves an existing agent with specified id.
     * 
     * @param agentId the agent's id.
     * @return an object representing the agent found, or null if an agent with specified id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static AgentDao findById(long agentId) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_agent ")
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

            if (!rs.next()) {
                log.debug("0 rows selected.");
                return null;
            }

            long id = rs.getLong("AGENT_ID");
            long requestId = rs.getLong("REQUEST_ID");
            long adminId = rs.getLong("ADMIN_ID");
            Date timeAccepted = rs.getTimestamp("TIME_ACCEPTED");
            Date timeJobRequest = rs.getTimestamp("TIME_JOBREQUEST");
            Date timeTerminated = rs.getTimestamp("TIME_TERMINATED");

            log.debug("1 row selected.");
            return new AgentDao(id, requestId, adminId, timeAccepted, timeJobRequest, timeTerminated);

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding agent by id: " + agentId);
        }
    }

    /**
     * Retrieves an existing agent with specified request id.
     * 
     * @param requestId id of the request this agent has been created for.
     * @return an object representing the agent found, or null if an agent with specified request id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static AgentDao findByRequestId(long requestId) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_agent ")
                .append("where ")
                .append("REQUEST_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + requestId);
            ss.setLong(1, requestId);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            if (!rs.next()) {
                log.debug("0 rows selected.");
                return null;
            }

            long agentId = rs.getLong("AGENT_ID");
            long reqId = rs.getLong("REQUEST_ID");
            long adminId = rs.getLong("ADMIN_ID");
            Date timeAccepted = rs.getTimestamp("TIME_ACCEPTED");
            Date timeJobRequest = rs.getTimestamp("TIME_JOBREQUEST");
            Date timeTerminated = rs.getTimestamp("TIME_TERMINATED");

            log.debug("1 row selected.");
            return new AgentDao(agentId, reqId, adminId, timeAccepted, timeJobRequest, timeTerminated);

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding agent by request id: " + requestId);
        }
    }

    /**
     * Retrieves all agents that have been created by a specific admin.
     * 
     * @param adminId the admin's id.
     * @return a set of objects, each representing an agent.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<AgentDao> findAllWithAdminId(long adminId) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_agent ")
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

            Set<AgentDao> agents = new HashSet<AgentDao>();

            int rows = 0;

            while (rs.next()) {

                rows++;
                long agentId = rs.getLong("AGENT_ID");
                long requestId = rs.getLong("REQUEST_ID");
                long admId = rs.getLong("ADMIN_ID");
                Date timeAccepted = rs.getTimestamp("TIME_ACCEPTED");
                Date timeJobRequest = rs.getTimestamp("TIME_JOBREQUEST");
                Date timeTerminated = rs.getTimestamp("TIME_TERMINATED");

                agents.add(new AgentDao(agentId, requestId, admId, timeAccepted, timeJobRequest, timeTerminated));
            }

            log.debug(rows + (rows == 1 ? " row " : " rows ") + "selected.");
            return agents;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding all agents for admin with id: " + adminId);
        }
    }

    /**
     * Retrieves all agents currently in store.
     * 
     * @return a set of objects, each representing an agent.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<AgentDao> findAll() throws DaoException {

        String sql = new StringBuilder()
                .append("select * from amdb_agent")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            Set<AgentDao> agents = new HashSet<AgentDao>();

            int rows = 0;

            while (rs.next()) {

                rows++;
                long agentId = rs.getLong("AGENT_ID");
                long requestId = rs.getLong("REQUEST_ID");
                long adminId = rs.getLong("ADMIN_ID");
                Date timeAccepted = rs.getTimestamp("TIME_ACCEPTED");
                Date timeJobRequest = rs.getTimestamp("TIME_JOBREQUEST");
                Date timeTerminated = rs.getTimestamp("TIME_TERMINATED");

                agents.add(new AgentDao(agentId, requestId, adminId, timeAccepted, timeJobRequest, timeTerminated));
            }

            log.debug(rows + (rows == 1 ? " row " : " rows ") + "selected.");
            return agents;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding all agents.");
        }
    }

    /**
     * Retrieves all agents that correspond to requests with a specific status.
     * 
     * @param requestStatus the requests' status.
     * @return a set of objects, each representing an agent.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<AgentDao> findAllWithRequestStatus(RequestStatus requestStatus) throws DaoException {

        String sql = new StringBuilder()
                .append("select * from ")
                .append("amdb_agent inner join amdb_request on ")
                .append("amdb_agent.REQUEST_ID = amdb_request.REQUEST_ID ")
                .append("where ")
                .append("STATUS = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + requestStatus.ordinal());
            ss.setInt(1, requestStatus.ordinal());

            ss.executeQuery();

            ResultSet rs = ss.getResultSet();

            Set<AgentDao> agents = new HashSet<AgentDao>();

            int rows = 0;

            while (rs.next()) {

                rows++;
                long agentId = rs.getLong("AGENT_ID");
                long requestId = rs.getLong("REQUEST_ID");
                long adminId = rs.getLong("ADMIN_ID");
                Date timeAccepted = rs.getTimestamp("TIME_ACCEPTED");
                Date timeJobRequest = rs.getTimestamp("TIME_JOBREQUEST");
                Date timeTerminated = rs.getTimestamp("TIME_TERMINATED");

                agents.add(new AgentDao(agentId, requestId, adminId, timeAccepted, timeJobRequest, timeTerminated));
            }

            log.debug(rows + (rows == 1 ? " row " : " rows ") + "selected.");
            return agents;

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while finding all agents.");
        }

    }

    /**
     * Updates an existing agent using the data provided.
     * 
     * @param agentId id of the existing agent being updated.
     * @param adminId id of the admin that is updating the agent.
     * @param timeAccepted the time of agent update.
     * @throws DaoException if the existing agent could not be updated or a data access error occurs.
     */
    public static void update(long agentId, long adminId, Date timeAccepted) throws DaoException {

        String sql = new StringBuilder()
                .append("update amdb_agent set ")
                .append("ADMIN_ID = ?, TIME_ACCEPTED = ?, TIME_JOBREQUEST = NULL, TIME_TERMINATED = NULL ")
                .append("where ")
                .append("AGENT_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + adminId);
            ss.setLong(1, adminId);

            log.debug("Setting parameter 2 to: " + timeAccepted);
            ss.setTimestamp(2, new Timestamp(timeAccepted.getTime()));

            log.debug("Setting parameter 3 to: " + agentId);
            ss.setLong(3, agentId);

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "updated.");

            if (rows != 1)
                throw new DaoException("Error while updating agent with id: " + agentId);

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while updating agent with id: " + agentId);
        }
    }

    /**
     * Sets an agent's time of jobs request to a specific value.
     * 
     * @param agentId the agent's id
     * @param timeJobRequest the agent's jobs request time.
     * @throws DaoException if the agent's jobs request time could not be set to the specified value or a data access error occurs.
     */
    public static void setTimeJobRequest(long agentId, Date timeJobRequest) throws DaoException {

        String sql = new StringBuilder()
                .append("update amdb_agent set ")
                .append("TIME_JOBREQUEST = ? ")
                .append("where ")
                .append("AGENT_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + timeJobRequest);
            ss.setTimestamp(1, new Timestamp(timeJobRequest.getTime()));

            log.debug("Setting parameter 2 to: " + agentId);
            ss.setLong(2, agentId);

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "updated.");

            if (rows != 1)
                throw new DaoException("Error while updating job request time on agent with id: " + agentId);

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while setting job request time to " + timeJobRequest + " on agent with id: " + agentId);
        }
    }

    /**
     * Sets an agent's termination time to a specific value.
     * 
     * @param agentId the agent's id.
     * @param timeTerminated the agent's termination time.
     * @throws DaoException if the agent's termination time could not be set to the specified value or a data access error occurs.
     */
    public static void setTimeTerminated(long agentId, Date timeTerminated) throws DaoException {

        String sql = new StringBuilder()
                .append("update amdb_agent set ")
                .append("TIME_TERMINATED = ? ")
                .append("where ")
                .append("AGENT_ID = ?")
                .toString();

        try (SynchronizedStatement ss = ConnectionSingleton.getInstance().getStatement(sql)) {

            log.debug("");
            log.debug("Executing query: " + sql);

            log.debug("Setting parameter 1 to: " + timeTerminated);
            ss.setTimestamp(1, new Timestamp(timeTerminated.getTime()));

            log.debug("Setting parameter 2 to: " + agentId);
            ss.setLong(2, agentId);

            int rows = ss.executeUpdate();
            log.debug(rows + (rows == 1 ? " row " : " rows ") + "updated.");

            if (rows != 1)
                throw new DaoException("Error while updating termination time on agent with id: " + agentId);

        } catch (SQLException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            throw new DaoException("Error while setting time terminated to " + timeTerminated + " on agent with id: " + agentId);
        }
    }

    long agentId;
    long requestId;
    long adminId;
    Date timeAccepted;
    Date timeJobRequest;
    Date timeTerminated;

    public long getAgentId() {
        return agentId;
    }

    public long getRequestId() {
        return requestId;
    }

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
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

    private AgentDao(long agentId, long requestId, long adminId, Date timeAccepted, Date timeJobRequest, Date timeTerminated) {
        super();
        this.agentId = agentId;
        this.requestId = requestId;
        this.adminId = adminId;
        this.timeAccepted = timeAccepted;
        this.timeJobRequest = timeJobRequest;
        this.timeTerminated = timeTerminated;
    }
}
