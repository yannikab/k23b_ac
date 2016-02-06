package k23b.ac.db.srv;

import java.util.Date;
import java.util.Set;

import k23b.ac.db.dao.CachedJobDao;
import k23b.ac.db.dao.CachedJobStatus;
import k23b.ac.db.dao.DaoException;
import k23b.ac.services.Logger;

/**
 * A service layer in which methods from the DAO layer are invoked for the manipulation of Cached Jobs.
 *
 */
public class CachedJobSrv {
    
    /**
     * Creation of a Cached Job or update of an already existing Cached Job.
     * 
     * @param jobId The jobId given by the AM 
     * @param agentId The agentId to which it has been appointed to
     * @param timeAssigned The time of assignment
     * @param timeSent The time of dispatch.
     * @param parameters The Job parameters.
     * @param periodic Boolean reflecting if the Job is periodic or not.
     * @param period The period of the Job.
     * @param status The Status of the Job.
     * @throws SrvException
     */
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
    
    /**
     * Finds all Cached Jobs from a certain Agent.
     * 
     * @param agentId The id of the desired Agent.
     * @return A Set of CachedJobDao Objects.
     * @throws SrvException
     */
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
