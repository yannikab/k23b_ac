package k23b.am.cc;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import k23b.am.dao.DaoException;
import k23b.am.dao.JobDao;

/**
 * Caching control layer for job objects.
 */
public class JobCC {

    private static final Logger log = Logger.getLogger(JobCC.class);

    private static volatile JobCache cache = null;

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
            cache = maxSize > 0 ? new JobCache(maxSize) : new JobCache();
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
     * Creates a new job using the data provided and caches it.
     * 
     * @param agentId id of the agent for which the job is being created.
     * @param adminId id of the admin that is creating the job.
     * @param timeAssigned time of job assignment.
     * @param params job parameters.
     * @param periodic job's periodic property.
     * @param period job's period, if periodic.
     * @return the newly created job's id or 0 if the job could not be retrieved for caching after its creation.
     * @throws DaoException if the job could not be created or retrieved for caching.
     */
    public static long create(long agentId, long adminId, Date timeAssigned, String params, boolean periodic, int period) throws DaoException {

        if (cacheDisabled())
            return JobDao.create(agentId, adminId, timeAssigned, params, periodic, period);

        long jobId = JobDao.create(agentId, adminId, timeAssigned, params, periodic, period);

        JobDao jd = JobDao.findById(jobId);

        if (jd == null)
            return 0;

        accessesAverted.decrementAndGet();

        cache.put(jd);

        return jd.getJobId();
    }

    /**
     * Retrieves an existing job with specified id and caches it.
     * 
     * @param jobId the job's id.
     * @return an object representing the job found, or null if a job with specified id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static JobDao findById(long jobId) throws DaoException {

        if (cacheDisabled())
            return JobDao.findById(jobId);

        JobDao jd = cache.getById(jobId);

        if (jd != null) {
            accessesAverted.incrementAndGet();
            log.debug(String.format("Returning cached job with id: %d (Data accesses averted: %d)", jobId, accessesAverted.get()));
            return jd;
        }

        jd = JobDao.findById(jobId);

        if (jd != null)
            cache.put(jd);

        return jd;
    }

    /**
     * Retrieves all jobs that have been assigned to a specific agent and caches them.
     * 
     * @param agentId the agent's id.
     * @return a set of objects, each representing a job.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<JobDao> findAllWithAgentId(long agentId) throws DaoException {

        if (cacheDisabled())
            return JobDao.findAllWithAgentId(agentId);

        Set<JobDao> jobs = JobDao.findAllWithAgentId(agentId);

        for (JobDao jd : jobs)
            cache.put(jd);

        return jobs;
    }

    /**
     * Retrieves all sent or not sent jobs that have been assigned to a specific agent and caches them.
     * 
     * @param agentId the agent's id.
     * @param sent the jobs' status, sent or not sent.
     * @return a set of objects, each representing a job.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<JobDao> findAllWithAgentId(long agentId, boolean sent) throws DaoException {

        if (cacheDisabled())
            return JobDao.findAllWithAgentId(agentId, sent);

        Set<JobDao> jobs = JobDao.findAllWithAgentId(agentId, sent);

        for (JobDao jd : jobs)
            cache.put(jd);

        return jobs;
    }

    /**
     * Retrieves all jobs that have been assigned by a specific admin and caches them.
     * 
     * @param adminId the admin's id.
     * @return a set of objects, each representing a job.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<JobDao> findAllWithAdminId(long adminId) throws DaoException {

        if (cacheDisabled())
            return JobDao.findAllWithAdminId(adminId);

        Set<JobDao> jobs = JobDao.findAllWithAdminId(adminId);

        for (JobDao jd : jobs)
            cache.put(jd);

        return jobs;
    }

    /**
     * Sets a job's sent time to a specific value. If the job was cached, updates the cached instance as well.
     * 
     * @param jobId the job's id.
     * @param timeSent the job's sent time.
     * @throws DaoException if the job's sent time could not be set to the specified value or a data access error occurs.
     */
    public static void setTimeSent(long jobId, Date timeSent) throws DaoException {

        if (cacheDisabled()) {
            JobDao.setTimeSent(jobId, timeSent);
            return;
        }

        try {

            JobDao.setTimeSent(jobId, timeSent);

            JobDao jd = cache.getById(jobId);

            if (jd == null)
                return;

            log.debug("Setting time sent to " + timeSent + " on cached job with id: " + jobId);
            jd.setTimeSent(timeSent);

        } catch (DaoException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            log.debug("Deleting cached job with id: " + jobId);
            cache.deleteById(jobId);

            throw e;
        }
    }

    /**
     * Sets a job's stop time to a specific value. If the job was cached, updates the cached instance as well.
     * 
     * @param jobId the job's id.
     * @param timeStopped the job's stop time.
     * @throws DaoException if the job's stop time could not be set to the specified value or a data access error occurs.
     */
    public static void setTimeStopped(long jobId, Date timeStopped) throws DaoException {

        if (cacheDisabled()) {
            JobDao.setTimeStopped(jobId, timeStopped);
            return;
        }

        try {

            JobDao.setTimeStopped(jobId, timeStopped);

            JobDao jd = cache.getById(jobId);

            if (jd == null)
                return;

            log.debug("Setting time stopped to " + timeStopped + " on cached job with id: " + jobId);
            jd.setTimeStopped(timeStopped);

        } catch (DaoException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            log.debug("Deleting cached job with id: " + jobId);
            cache.deleteById(jobId);

            throw e;
        }
    }
}
