package k23b.ac.db.srv;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import k23b.ac.db.dao.CachedJobDao;
import k23b.ac.db.dao.CachedJobStatus;
import k23b.ac.db.dao.DaoException;
import k23b.ac.services.Logger;

public class CachedJobSrv {

    public static void createOrUpdate(long jobId, long agentId, Date timeAssigned, Date timeSent, String parameters, boolean periodic, int period, CachedJobStatus status) throws SrvException {

        synchronized (CachedJobDao.class) {

            try {

                if (!CachedJobDao.exists(jobId))
                    CachedJobDao.create(jobId, agentId, timeAssigned, timeSent, parameters, periodic, period, status);
                else
                    CachedJobDao.update(jobId, agentId, timeAssigned, timeSent, parameters, periodic, period, status);

            } catch (DaoException e) {

                Logger.error(CachedJobSrv.class.getSimpleName(), e.getMessage());

                throw new SrvException("Data access error while creating or updating job with id: " + jobId);
            }
        }
    }

    public static Set<CachedJobDao> findAllWithAgentId(long agentId) throws SrvException {

        synchronized (CachedJobDao.class) {

            try {

                return CachedJobDao.findAllWithAgentId(agentId);

            } catch (DaoException e) {

                Logger.error(CachedJobSrv.class.getSimpleName(), e.getMessage());

                throw new SrvException("Data access error while finding all jobs with agentId: " + agentId);
            }
        }
    }
}
