package k23b.ac.db.srv;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import k23b.ac.db.dao.CachedAgentDao;
import k23b.ac.db.dao.CachedAgentStatus;
import k23b.ac.db.dao.DaoException;
import k23b.ac.services.Logger;

public class CachedAgentSrv {

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
