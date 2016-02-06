package k23b.ac.db.srv;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import k23b.ac.db.dao.CachedAgentDao;
import k23b.ac.db.dao.CachedAgentStatus;
import k23b.ac.db.dao.DaoException;
import k23b.ac.services.Logger;

/**
 * A service layer in which methods from the DAO layer are invoked for the manipulation of Cached Agents.
 *
 */
public class CachedAgentSrv {
    
    /**
     * Creation of a Cached Agent or update of an already existing Cached Agent.
     * 
     * @param agentId The agentId given by the AM.
     * @param agentHash The agentHash originating by the Agent Statistics
     * @param timeAccepted The time of Agent acceptance
     * @param timeJobRequest The time of last Job request
     * @param timeTerminated The time of Agent termination.
     * @param agentStatus The Status of the Agent
     * @throws SrvException
     */
    public static void createOrUpdate(long agentId, String agentHash, Date timeAccepted, Date timeJobRequest, Date timeTerminated, CachedAgentStatus agentStatus) throws SrvException {

        synchronized (CachedAgentDao.class) {

            try {

                if (!CachedAgentDao.exists(agentId))
                    CachedAgentDao.create(agentId, agentHash, timeAccepted, timeJobRequest, timeTerminated, agentStatus);
                else
                    CachedAgentDao.update(agentId, agentHash, timeAccepted, timeJobRequest, timeTerminated, agentStatus);

            } catch (DaoException e) {

                Logger.error(CachedAgentSrv.class.getSimpleName(), e.getMessage());

                throw new SrvException("Data access error while creating or updating agent with id: " + agentId);
            }
        }
    }
    
    /**
     * Finds all the Cached Agents in the Database.
     * 
     * @return A Set of CachedAgentDao Objects.
     * @throws SrvException
     */
    public static Set<CachedAgentDao> findAll() throws SrvException {

        synchronized (CachedAgentDao.class) {

            try {

                return CachedAgentDao.findAll();

            } catch (DaoException e) {

                Logger.error(CachedAgentSrv.class.getSimpleName(), e.getMessage());

                throw new SrvException("Data access error while finding all agents.");
            }
        }
    }
}
