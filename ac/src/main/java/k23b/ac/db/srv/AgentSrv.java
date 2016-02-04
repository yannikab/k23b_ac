package k23b.ac.db.srv;

import java.util.Date;
import java.util.Set;

import k23b.ac.db.dao.AgentDao;
import k23b.ac.db.dao.AgentStatus;
import k23b.ac.db.dao.DaoException;
import k23b.ac.services.Logger;

public class AgentSrv {

    public static void createOrUpdate(long agentId, String agentHash, Date timeAccepted, Date timeJobRequest, Date timeTerminated, AgentStatus agentStatus) throws SrvException {

        synchronized (AgentDao.class) {

            try {

                if (!AgentDao.agentExists(agentId))
                    AgentDao.create(agentId, agentHash, timeAccepted, timeJobRequest, timeTerminated, agentStatus);
                else
                    AgentDao.update(agentId, agentHash, timeAccepted, timeJobRequest, timeTerminated, agentStatus);

            } catch (DaoException e) {

                Logger.error(AgentSrv.class.getSimpleName(), e.getMessage());

                throw new SrvException("Data access error while creating or updating agent with id: " + agentId);
            }
        }
    }

    public static Set<AgentDao> findAll() throws SrvException {

        synchronized (AgentDao.class) {

            try {

                return AgentDao.findAll();

            } catch (DaoException e) {

                Logger.error(AgentSrv.class.getSimpleName(), e.getMessage());

                throw new SrvException("Data access error while finding all agents.");
            }
        }
    }
}
