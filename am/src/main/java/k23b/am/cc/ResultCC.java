package k23b.am.cc;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import k23b.am.dao.DaoException;
import k23b.am.dao.ResultDao;

/**
 * Caching control layer for result objects.
 */
public class ResultCC {

    private static final Logger log = Logger.getLogger(ResultCC.class);

    private static volatile ResultCache cache = null;

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
            cache = maxSize > 0 ? new ResultCache(maxSize) : new ResultCache();
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
     * Creates a new result using the data provided and caches it.
     * 
     * @param jobId id of the job for which the result is being created.
     * @param output the result's job output.
     * @return the newly created result's id or 0 if the result could not be retrieved for caching after its creation.
     * @throws DaoException if the result could not be created or retrieved for caching.
     */
    public static long create(long jobId, String output) throws DaoException {

        if (cacheDisabled())
            return ResultDao.create(jobId, output);

        long resultId = ResultDao.create(jobId, output);

        ResultDao rd = ResultDao.findById(resultId);

        if (rd == null)
            return 0;

        accessesAverted.decrementAndGet();

        cache.put(rd);

        return rd.getResultId();
    }

    /**
     * Retrieves an existing result with specified id and caches it.
     * 
     * @param resultId the result's id.
     * @return an object representing the result found, or null if a result with specified id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static ResultDao findById(long resultId) throws DaoException {

        if (cacheDisabled())
            return ResultDao.findById(resultId);

        ResultDao rd = cache.getById(resultId);

        if (rd != null) {
            accessesAverted.incrementAndGet();
            log.debug(String.format("Returning cached result with id: %d (Data accesses averted: %d)", resultId, accessesAverted.get()));
            return rd;
        }

        rd = ResultDao.findById(resultId);

        if (rd != null)
            cache.put(rd);

        return rd;
    }

    /**
     * Retrieves all results that have been received for a specific job and caches them.
     * 
     * @param jobId the job's id.
     * @return a set of objects, each representing a result.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<ResultDao> findAllWithJobId(long jobId) throws DaoException {

        if (cacheDisabled())
            return ResultDao.findAllWithJobId(jobId);

        Set<ResultDao> results = ResultDao.findAllWithJobId(jobId);

        for (ResultDao rd : results)
            cache.put(rd);

        return results;
    }

    /**
     * Retrieves all results that have been received within a specific time range and caches them.
     * 
     * @param startTime start of the time range.
     * @param endTime end of the time range.
     * @return a set of objects, each representing a result.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<ResultDao> findAllWithinDates(Date startTime, Date endTime) throws DaoException {

        if (cacheDisabled())
            return ResultDao.findAllWithinDates(startTime, endTime);

        Set<ResultDao> results = ResultDao.findAllWithinDates(startTime, endTime);

        for (ResultDao rd : results)
            cache.put(rd);

        return results;
    }

    /**
     * Retrieves all results received for a specific agent within a specific time range and caches them.
     * 
     * @param agentId the agent's id.
     * @param startTime start of the time range.
     * @param endTime end of the time range.
     * @return a set of objects, each representing a result.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<ResultDao> findAllWithinDates(long agentId, Date startTime, Date endTime) throws DaoException {

        if (cacheDisabled())
            return ResultDao.findAllWithinDates(agentId, startTime, endTime);

        Set<ResultDao> results = ResultDao.findAllWithinDates(agentId, startTime, endTime);

        for (ResultDao rd : results)
            cache.put(rd);

        return results;
    }
}
