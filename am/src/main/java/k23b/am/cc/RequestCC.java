package k23b.am.cc;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import k23b.am.dao.DaoException;
import k23b.am.dao.RequestDao;
import k23b.am.dao.RequestStatus;

/**
 * Caching control layer for request objects.
 */
public class RequestCC {

    private static final Logger log = Logger.getLogger(RequestCC.class);

    private static volatile RequestCache cache = null;

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
            cache = maxSize > 0 ? new RequestCache(maxSize) : new RequestCache();
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
     * Creates a new request using the data provided and caches it.
     * 
     * @param hash the agent's hash value.
     * @param deviceName the agent's device name.
     * @param interfaceIP the agent's interface IP address.
     * @param interfaceMAC the agent's interface MAC address.
     * @param osVersion the agent's operating system version.
     * @param nmapVersion the agent's nmap version.
     * @param status the request's status.
     * @param timeReceived time the request was received.
     * @return the newly created request's id or 0 if the request could not be retrieved for caching after its creation.
     * @throws DaoException if the request could not be created or retrieved for caching.
     */
    public static long create(String hash, String deviceName, String interfaceIP, String interfaceMAC, String osVersion, String nmapVersion, RequestStatus status, Date timeReceived) throws DaoException {

        if (cacheDisabled())
            return RequestDao.create(hash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion, status, timeReceived);

        long requestId = RequestDao.create(hash, deviceName, interfaceIP, interfaceMAC, osVersion, nmapVersion, status, timeReceived);

        RequestDao rd = RequestDao.findById(requestId);

        if (rd == null)
            return 0;

        accessesAverted.decrementAndGet();

        cache.put(rd);

        return rd.getRequestId();
    }

    /**
     * Retrieves an existing request with specified id and caches it.
     * 
     * @param requestId the request's id.
     * @return an object representing the request found, or null if a request with specified id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static RequestDao findById(long requestId) throws DaoException {

        if (cacheDisabled())
            return RequestDao.findById(requestId);

        RequestDao rd = cache.getById(requestId);

        if (rd != null) {
            accessesAverted.incrementAndGet();
            log.debug(String.format("Returning cached request with id: %d (Data accesses averted: %d)", requestId, accessesAverted.get()));
            return rd;
        }

        rd = RequestDao.findById(requestId);

        if (rd != null)
            cache.put(rd);

        return rd;
    }

    /**
     * Retrieves an existing request with specified hash and caches it.
     * 
     * @param hash the request's hash.
     * @return an object representing the request found, or null if a request with specified hash was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static RequestDao findByHash(String hash) throws DaoException {

        if (cacheDisabled())
            return RequestDao.findByHash(hash);

        RequestDao rd = cache.getByHash(hash);

        if (rd != null) {
            accessesAverted.incrementAndGet();
            log.debug(String.format("Returning cached request with hash: %s (Data accesses averted: %d)", hash, accessesAverted.get()));
            return rd;
        }

        rd = RequestDao.findByHash(hash);

        if (rd != null)
            cache.put(rd);

        return rd;
    }

    /**
     * Retrieves all requests currently in store and caches them.
     * 
     * @return a set of objects, each representing a request.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<RequestDao> findAll() throws DaoException {

        if (cacheDisabled())
            return RequestDao.findAll();

        Set<RequestDao> requests = RequestDao.findAll();

        for (RequestDao rd : requests)
            cache.put(rd);

        return requests;
    }

    /**
     * Retrieves all requests that have a specific status and caches them.
     * 
     * @param requestStatus status of the requests.
     * @return a set of objects, each representing a request.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<RequestDao> findAllWithStatus(RequestStatus requestStatus) throws DaoException {

        if (cacheDisabled())
            return RequestDao.findAllWithStatus(requestStatus);

        Set<RequestDao> requests = findAllWithStatus(requestStatus);

        for (RequestDao rd : requests)
            cache.put(rd);

        return requests;
    }

    /**
     * Sets the status of a request to a specific value. If the request was cached, updates the cached instance as well.
     * 
     * @param requestId the request's id.
     * @param requestStatus the request's status.
     * @throws DaoException if the request's status could not be changed to the specified value or a data access error occurs.
     */
    public static void setStatus(long requestId, RequestStatus requestStatus) throws DaoException {

        if (cacheDisabled()) {
            RequestDao.setStatus(requestId, requestStatus);
            return;
        }

        try {

            RequestDao.setStatus(requestId, requestStatus);

            RequestDao rd = cache.getById(requestId);

            if (rd == null)
                return;

            log.debug("Setting status to " + requestStatus + " on cached request with id: " + requestId);
            rd.setRequestStatus(requestStatus);

        } catch (DaoException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            log.debug("Deleting cached request with id: " + requestId);
            cache.deleteById(requestId);

            throw e;
        }
    }

    /**
     * Sets a request's received time to a specific value. If the request was cached, updates the cached instance as well.
     * 
     * @param requestId the request's id.
     * @param timeReceived the request's received time.
     * @throws DaoException if the request's received time could not be changed to the specified value or a data access error occurs.
     */
    public static void setTimeReceived(long requestId, Date timeReceived) throws DaoException {

        if (cacheDisabled()) {
            RequestDao.setTimeReceived(requestId, timeReceived);
            return;
        }

        try {

            RequestDao.setTimeReceived(requestId, timeReceived);

            RequestDao rd = cache.getById(requestId);

            if (rd == null)
                return;

            log.debug("Setting time received to " + timeReceived + " on cached request with id: " + requestId);
            rd.setTimeReceived(timeReceived);

        } catch (DaoException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            log.debug("Deleting cached request with id: " + requestId);
            cache.deleteById(requestId);

            throw e;
        }
    }
}
