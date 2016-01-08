package k23b.am.cc;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import k23b.am.dao.AgentDao;
import k23b.am.dao.DaoException;
import k23b.am.dao.RequestStatus;

/**
 * Caching control layer for agent objects.
 */
public class AgentCC {

    private static final Logger log = Logger.getLogger(AgentCC.class);

    private static volatile AgentCache cache = null;

    private static boolean cacheDisabled() {

        return cache == null;
    }

    private static final AtomicLong accessesAverted = new AtomicLong(0);

    /**
     * Retrieves the data accesses averted statistic for this cache.
     * 
     * @return the number of data accesses averted because of a cache hit
     */
    public static long getAccessesAverted() {

        return accessesAverted.get();
    }

    /**
     * Initializes the cache, without a maximum size restriction.
     */
    public static void initCache() {

        initCache(0);
    }

    /**
     * Initializes the cache, with a maximum size.
     */
    public static synchronized void initCache(long maxSize) {

        if (cache == null)
            cache = maxSize > 0 ? new AgentCache(maxSize) : new AgentCache();
        else
            cache.clear();

        accessesAverted.set(0);
    }

    /**
     * Clears the cache.
     */
    public static void clearCache() {

        if (cacheDisabled())
            return;

        cache.clear();

        accessesAverted.set(0);
    }

    /**
     * Creates a new agent using the data provided and caches it.
     * 
     * @param requestId id of the request for which the agent is being created.
     * @param adminId id of the admin that is creating the agent.
     * @param timeAccepted the time of agent creation.
     * @return the newly created agent's id or 0 if the agent could not be retrieved for caching after its creation.
     * @throws DaoException if the agent could not be created or retrieved for caching.
     */
    public static long create(long requestId, long adminId, Date timeAccepted) throws DaoException {

        if (cacheDisabled())
            return AgentDao.create(requestId, adminId, timeAccepted);

        long agentId = AgentDao.create(requestId, adminId, timeAccepted);

        AgentDao ad = AgentDao.findById(agentId);

        if (ad == null)
            return 0;

        accessesAverted.decrementAndGet();

        cache.put(ad);

        return ad.getAgentId();
    }

    /**
     * Retrieves an existing agent with specified id and caches it.
     * 
     * @param agentId the agent's id.
     * @return an object representing the agent found, or null if an agent with specified id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static AgentDao findById(long agentId) throws DaoException {

        if (cacheDisabled())
            return AgentDao.findById(agentId);

        AgentDao ad = cache.getById(agentId);

        if (ad != null) {
            accessesAverted.incrementAndGet();
            log.debug(String.format("Returning cached agent with id: %d (Data accesses averted: %d)", agentId, accessesAverted.get()));
            return ad;
        }

        ad = AgentDao.findById(agentId);

        if (ad != null)
            cache.put(ad);

        return ad;
    }

    /**
     * Retrieves an existing agent with specified request id and caches it.
     * 
     * @param requestId id of the request this agent has been created for.
     * @return an object representing the agent found, or null if an agent with specified request id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static AgentDao findByRequestId(long requestId) throws DaoException {

        if (cacheDisabled())
            return AgentDao.findByRequestId(requestId);

        AgentDao ad = cache.getByRequestId(requestId);

        if (ad != null) {
            accessesAverted.incrementAndGet();
            log.debug(String.format("Returning cached agent with request id: %d (Data accesses averted: %d)", requestId, accessesAverted.get()));
            return ad;
        }

        ad = AgentDao.findByRequestId(requestId);

        if (ad != null)
            cache.put(ad);

        return ad;
    }

    /**
     * Retrieves all agents that have been created by a specific admin and caches them.
     * 
     * @param adminId the admin's id.
     * @return a set of objects, each representing an agent.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<AgentDao> findAllWithAdminId(long adminId) throws DaoException {

        if (cacheDisabled())
            return AgentDao.findAllWithAdminId(adminId);

        Set<AgentDao> agents = AgentDao.findAllWithAdminId(adminId);

        for (AgentDao ad : agents)
            cache.put(ad);

        return agents;
    }

    /**
     * Retrieves all agents currently in store and caches them.
     * 
     * @return a set of objects, each representing an agent.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<AgentDao> findAll() throws DaoException {

        if (cacheDisabled())
            return AgentDao.findAll();

        Set<AgentDao> agents = AgentDao.findAll();

        for (AgentDao ad : agents)
            cache.put(ad);

        return agents;
    }

    /**
     * Retrieves all agents that correspond to requests with a specific status and caches them.
     * 
     * @param requestStatus the requests' status.
     * @return a set of objects, each representing an agent.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<AgentDao> findAllWithRequestStatus(RequestStatus requestStatus) throws DaoException {

        if (cacheDisabled())
            return AgentDao.findAllWithRequestStatus(requestStatus);

        Set<AgentDao> agents = AgentDao.findAllWithRequestStatus(requestStatus);

        for (AgentDao ad : agents)
            cache.put(ad);

        return agents;
    }

    /**
     * Updates an existing agent using the data provided. If the agent was cached, updates the cached instance as well.
     * 
     * @param agentId id of the existing agent being updated.
     * @param adminId id of the admin that is updating the agent.
     * @param timeAccepted the time of agent update.
     * @throws DaoException if the existing agent could not be updated or a data access error occurs.
     */
    public static void update(long agentId, long adminId, Date timeAccepted) throws DaoException {

        if (cacheDisabled()) {
            AgentDao.update(agentId, adminId, timeAccepted);
            return;
        }

        try {

            AgentDao.update(agentId, adminId, timeAccepted);

            AgentDao ad = cache.getById(agentId);

            if (ad == null)
                return;

            log.debug("Setting admin id to " + adminId + " on cached agent with id: " + agentId);
            ad.setAdminId(adminId);

            log.debug("Setting time accepted to " + timeAccepted + " on cached agent with id: " + agentId);
            ad.setTimeAccepted(timeAccepted);

        } catch (DaoException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            log.debug("Deleting cached agent with id: " + agentId);
            cache.deleteById(agentId);

            throw e;
        }
    }

    /**
     * Sets an agent's time of jobs request to a specific value. If the agent was cached, updates the cached instance as well.
     * 
     * @param agentId the agent's id
     * @param timeJobRequest the agent's jobs request time.
     * @throws DaoException if the agent's jobs request time could not be set to the specified value or a data access error occurs.
     */
    public static void setTimeJobRequest(long agentId, Date timeJobRequest) throws DaoException {

        if (cacheDisabled()) {
            AgentDao.setTimeJobRequest(agentId, timeJobRequest);
            return;
        }

        try {

            AgentDao.setTimeJobRequest(agentId, timeJobRequest);

            AgentDao ad = cache.getById(agentId);

            if (ad == null)
                return;

            log.debug("Setting job request time to " + timeJobRequest + " on cached agent with id: " + agentId);
            ad.setTimeJobRequest(timeJobRequest);

        } catch (DaoException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            log.debug("Deleting cached agent with id: " + agentId);
            cache.deleteById(agentId);

            throw e;
        }
    }

    /**
     * Sets an agent's termination time to a specific value. If the agent was cached, updates the cached instance as well.
     * 
     * @param agentId the agent's id.
     * @param timeTerminated the agent's termination time.
     * @throws DaoException if the agent's termination time could not be set to the specified value or a data access error occurs.
     */
    public static void setTimeTerminated(long agentId, Date timeTerminated) throws DaoException {

        if (cacheDisabled()) {
            AgentDao.setTimeTerminated(agentId, timeTerminated);
            return;
        }

        try {

            AgentDao.setTimeTerminated(agentId, timeTerminated);

            AgentDao ad = cache.getById(agentId);

            if (ad == null)
                return;

            log.debug("Setting agent termination time to " + timeTerminated + " on cached agent with id: " + agentId);
            ad.setTimeTerminated(timeTerminated);

        } catch (DaoException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            log.debug("Deleting cached agent with id: " + agentId);
            cache.deleteById(agentId);

            throw e;
        }
    }
}
