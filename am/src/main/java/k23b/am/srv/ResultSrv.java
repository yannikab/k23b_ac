package k23b.am.srv;

import java.util.Date;
import java.util.Set;

import k23b.am.cc.AgentCC;
import k23b.am.cc.JobCC;
import k23b.am.cc.ResultCC;
import k23b.am.dao.DaoException;
import k23b.am.dao.JobDao;
import k23b.am.dao.ResultDao;

/**
 * Service layer for result objects.
 */
public class ResultSrv {

    private static volatile boolean lock = true;

    /**
     * Sets the service layer's underlying data store locking policy.
     * 
     * @param lock if set to true, individual underlying data stores will be locked upon access.
     */
    public static void setLock(boolean lock) {

        ResultSrv.lock = lock;
    }

    /**
     * Creates a result for a specific job.
     * 
     * @param jobId id of the job for which the result is being created.
     * @param output the result's job output.
     * @return the created result object containing its generated id, or null if the result was not found after creating it.
     * @throws SrvException if a job with specified id does not exist, the job has not been sent, the result could not be created, or a data access error occurs.
     */
    public static ResultDao create(long jobId, String output) throws SrvException {

        try {

            synchronized (lock ? JobCC.class : new Object()) {

                JobDao j = JobCC.findById(jobId);

                if (j == null)
                    throw new SrvException("Can not create result. Could not find job with id: " + jobId);

                if (!j.getSent())
                    throw new SrvException("Can not create result. Job with id " + jobId + " has not been sent.");

                synchronized (lock ? ResultCC.class : new Object()) {

                    long resultId = ResultCC.create(jobId, output);

                    if (resultId == 0)
                        throw new SrvException("Could not create result for job with id: " + jobId);

                    return ResultCC.findById(resultId);
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while creating result for job with id: " + jobId);
        }
    }

    /**
     * Retrieves the result with specified id.
     * 
     * @param resultId the result's id.
     * @return the result found or null if a result with specified id was not found.
     * @throws SrvException if a data access error occurs.
     */
    public static ResultDao findById(long resultId) throws SrvException {

        try {

            synchronized (lock ? ResultCC.class : new Object()) {

                return ResultCC.findById(resultId);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding agent by id: " + resultId);
        }
    }

    /**
     * Retrieves all results that have been received for a specific job.
     * 
     * @param jobId the job's id.
     * @return a set of objects, each representing a result.
     * @throws SrvException if a job with specified id does not exist, or a data access error occurs.
     */
    public static Set<ResultDao> findAllWithJobId(long jobId) throws SrvException {

        try {

            synchronized (lock ? JobCC.class : new Object()) {

                if (JobCC.findById(jobId) == null)
                    throw new SrvException("Can not find results. Job does not exist with id: " + jobId);

                synchronized (lock ? ResultCC.class : new Object()) {

                    return ResultCC.findAllWithJobId(jobId);
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding results for job with id: " + jobId);
        }
    }

    /**
     * Retrieves all results received for a specific agent within a specific time range.
     * 
     * @param agentId the agent's id.
     * @param startTime start of the time range.
     * @param endTime end of the time range.
     * @return a set of objects, each representing a result.
     * @throws SrvException if an agent with specified id does not exist, or a data access error occurs.
     */
    public static Set<ResultDao> findAllWithinDates(long agentId, Date startTime, Date endTime) throws SrvException {

        try {

            synchronized (lock ? AgentCC.class : new Object()) {

                if (AgentCC.findById(agentId) == null)
                    throw new SrvException("Can not find results. Agent does not exist with id: " + agentId);

                synchronized (lock ? ResultCC.class : new Object()) {

                    return ResultCC.findAllWithinDates(agentId, startTime, endTime);
                }
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding results within times for agent with id: " + agentId);
        }
    }

    /**
     * Retrieves all results that have been received within a specific time range.
     * 
     * @param startTime start of the time range.
     * @param endTime end of the time range.
     * @return a set of objects, each representing a result.
     * @throws SrvException if a data access error occurs.
     */
    public static Set<ResultDao> findAllWithinDates(Date startTime, Date endTime) throws SrvException {

        try {

            synchronized (lock ? ResultCC.class : new Object()) {

                return ResultCC.findAllWithinDates(startTime, endTime);
            }

        } catch (DaoException e) {
            // e.printStackTrace();
            throw new SrvException("Data access error while finding results between times " + startTime + " and " + endTime + ".");
        }
    }
}
