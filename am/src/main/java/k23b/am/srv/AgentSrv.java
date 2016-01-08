package k23b.am.srv;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

import k23b.am.cc.AdminCC;
import k23b.am.cc.AgentCC;
import k23b.am.cc.RequestCC;
import k23b.am.dao.AdminDao;
import k23b.am.dao.AgentDao;
import k23b.am.dao.DaoException;
import k23b.am.dao.RequestDao;
import k23b.am.dao.RequestStatus;

/**
 * Service layer for agent objects.
 */
public class AgentSrv {

    private static volatile boolean lock = true;

    /**
     * Sets the service layer's underlying data store locking policy.
     * 
     * @param lock if set to true, individual underlying data stores will be locked upon access.
     */
    public static void setLock(boolean lock) {

        AgentSrv.lock = lock;
    }

    /**
     * Creates or updates an agent for a specific request that is not currently accepted, on behalf of a specific admin.
     * 
     * @param requestId id of the request for which the agent is being created.
     * @param adminId id of the admin that is creating the agent.
     * @return the created agent object containing its generated id, or null if the agent was not found after creating it.
     * @throws SrvException if request id or admin id are invalid, the request's status is already accepted, the admin is not logged in, the agent could not be created or updated, or a data access error occurs.
     */
    public static AgentDao create(long requestId, long adminId) throws SrvException {

        try {

            synchronized (lock ? RequestCC.class : new Object()) {

                // make sure that request exists and that its status is not accepted
                RequestDao rd = RequestCC.findById(requestId);

                if (rd == null)
                    throw new SrvException("Can not create agent. Could not find request with id: " + requestId);

                if (rd.getRequestStatus() == RequestStatus.ACCEPTED)
                    throw new SrvException("Can not create agent. Status is already accepted for request with id: " + requestId);

                synchronized (lock ? AdminCC.class : new Object()) {

                    // make sure that the admin creating the agent exists and is logged in

                    AdminDao ad = AdminCC.findById(adminId);

                    if (ad == null)
                        throw new SrvException("Can not create agent. Could not find admin with id: " + adminId);

                    if (!ad.getActive())
                        throw new SrvException("Can not create agent. Admin with id " + adminId + " is not logged in.");

                    synchronized (lock ? AgentCC.class : new Object()) {

                        // check if agent already exists, if it doesn't we create it, otherwise update it
                        AgentDao agent = AgentCC.findByRequestId(requestId);

                        if (agent == null) {

                            long agentId = AgentCC.create(requestId, adminId, Date.from(Instant.now()));

                            if (agentId == 0)
                                throw new SrvException("Could not create agent for request with id: " + requestId);

                            agent = AgentCC.findById(agentId);

                        } else {

                            AgentCC.update(agent.getAgentId(), adminId, Date.from(Instant.now()));
                        }

                        RequestCC.setStatus(requestId, RequestStatus.ACCEPTED);

                        return agent;
                    }
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while creating agent for request with id: " + requestId);
        }
    }

    /**
     * Retrieves the agent with specified id.
     * 
     * @param agentId the agent's id.
     * @return the agent found or null if an agent with specified id was not found.
     * @throws SrvException if a data access error occurs.
     */
    public static AgentDao findById(long agentId) throws SrvException {

        try {

            synchronized (lock ? AgentCC.class : new Object()) {

                return AgentCC.findById(agentId);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding agent by id: " + agentId);
        }
    }

    /**
     * Retrieves the agent with specified request id.
     * 
     * @param requestId the agent's request id.
     * @return the agent found or null if an agent with specified request id was not found.
     * @throws SrvException if a data access error occurs.
     */
    public static AgentDao findByRequestId(long requestId) throws SrvException {

        try {

            synchronized (lock ? AgentCC.class : new Object()) {

                return AgentCC.findByRequestId(requestId);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding agent by request id: " + requestId);
        }
    }

    /**
     * Retrieves all agents created or updated by a specific admin.
     * 
     * @param adminId the admin's id.
     * @return a set of objects, each representing an agent.
     * @throws SrvException if an admin with specified id does not exist, or a data access error occurs.
     */
    public static Set<AgentDao> findAllWithAdminId(long adminId) throws SrvException {

        try {

            synchronized (lock ? AdminCC.class : new Object()) {

                if (AdminCC.findById(adminId) == null)
                    throw new SrvException("Can not find agents. Admin does not exist with id: " + adminId);

                synchronized (lock ? AgentCC.class : new Object()) {

                    return AgentCC.findAllWithAdminId(adminId);
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding agents for admin with id: " + adminId);
        }
    }

    /**
     * Retrieves all agents currently in store.
     * 
     * @return a set of objects, each representing an agent.
     * @throws SrvException if a data access error occurs.
     */
    public static Set<AgentDao> findAll() throws SrvException {

        try {

            synchronized (lock ? AgentCC.class : new Object()) {

                return AgentCC.findAll();
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding all agents.");
        }
    }

    /**
     * Retrieves all agents that correspond to requests with a specific status.
     * 
     * @param requestStatus the requests' status.
     * @return a set of objects, each representing an agent.
     * @throws SrvException if a data access error occurs.
     */
    public static Set<AgentDao> findAllWithRequestStatus(RequestStatus requestStatus) throws SrvException {

        try {

            synchronized (lock ? AgentCC.class : new Object()) {

                if (requestStatus == RequestStatus.ALL)
                    return AgentCC.findAll();
                else
                    return AgentCC.findAllWithRequestStatus(requestStatus);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding all agents.");
        }
    }

    /**
     * Sets an agent's jobs request time to the current time.
     * 
     * @param agentId the agent's id
     * @throws SrvException if an agent with specified id does not exist, its jobs request time could not be set, or a data access error occurs.
     */
    public static void jobsRequested(long agentId) throws SrvException {

        try {

            synchronized (lock ? AgentCC.class : new Object()) {

                if (AgentCC.findById(agentId) == null)
                    throw new SrvException("Can not find agent. Agent does not exist with id: " + agentId);

                AgentCC.setTimeJobRequest(agentId, Date.from(Instant.now()));
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while setting job request time for agent with id: " + agentId);
        }
    }

    /**
     * Sets an agent's termination time to the current time.
     * 
     * @param agentId the agent's id
     * @throws SrvException if an agent with specified id does not exist, its termination time could not be set, or a data access error occurs.
     */
    public static void terminate(long agentId) throws SrvException {

        try {

            synchronized (lock ? AgentCC.class : new Object()) {

                AgentDao a = AgentCC.findById(agentId);

                if (a == null)
                    throw new SrvException("Can not terminate agent. Could not find agent with id: " + agentId);

                AgentCC.setTimeTerminated(agentId, Date.from(Instant.now()));
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while setting sent to true for job with id: " + agentId);
        }
    }
}
