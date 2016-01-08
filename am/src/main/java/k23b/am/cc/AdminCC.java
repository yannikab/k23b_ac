package k23b.am.cc;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import k23b.am.dao.AdminDao;
import k23b.am.dao.DaoException;

/**
 * Caching control layer for admin objects.
 */
public class AdminCC {

    private static final Logger log = Logger.getLogger(AdminCC.class);

    private static volatile AdminCache cache = null;

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
            cache = maxSize > 0 ? new AdminCache(maxSize) : new AdminCache();
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
     * Creates a new admin using the data provided and caches it.
     * 
     * @param username the admin's username.
     * @param password the admin's password.
     * @param active the admin's active status.
     * @return the newly created admin's id or 0 if the admin could not be retrieved for caching after its creation.
     * @throws DaoException if the admin could not be created or retrieved for caching.
     */
    public static long create(String username, String password, boolean active) throws DaoException {

        if (cacheDisabled())
            return AdminDao.create(username, password, active);

        long adminId = AdminDao.create(username, password, active);

        AdminDao ad = AdminDao.findById(adminId);

        if (ad == null)
            return 0;

        accessesAverted.decrementAndGet();

        cache.put(ad);

        return ad.getAdminId();
    }

    /**
     * Retrieves an existing admin with specified id and caches it.
     * 
     * @param adminId the admin's id.
     * @return an object representing the admin found, or null if an admin with specified id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static AdminDao findById(long adminId) throws DaoException {

        if (cacheDisabled())
            return AdminDao.findById(adminId);

        AdminDao ad = cache.getById(adminId);

        if (ad != null) {
            accessesAverted.incrementAndGet();
            log.debug(String.format("Returning cached admin with id: %d (Data accesses averted: %d)", adminId, accessesAverted.get()));
            return ad;
        }

        ad = AdminDao.findById(adminId);

        if (ad != null)
            cache.put(ad);

        return ad;
    }

    /**
     * Retrieves an existing admin with specified username and caches it.
     * 
     * @param username the admin's username.
     * @return an object representing the admin found, or null if an admin with specified username was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static AdminDao findByUsername(String username) throws DaoException {

        if (cacheDisabled())
            return AdminDao.findByUsername(username);

        AdminDao ad = cache.getByUsername(username);

        if (ad != null) {
            accessesAverted.incrementAndGet();
            log.debug(String.format("Returning cached admin with username: %s (Data accesses averted: %d)", username, accessesAverted.get()));
            return ad;
        }

        ad = AdminDao.findByUsername(username);

        if (ad != null)
            cache.put(ad);

        return ad;
    }

    /**
     * Sets the active status of an admin to a specific value. If the admin was cached, updates the cached instance as well.
     * 
     * @param adminId the admin's id.
     * @param active the admin's active status.
     * @throws DaoException if the admin's active status could not be changed to the specified value or a data access error occurs.
     */
    public static void setActive(long adminId, boolean active) throws DaoException {

        if (cacheDisabled()) {
            AdminDao.setActive(adminId, active);
            return;
        }

        try {

            AdminDao.setActive(adminId, active);

            AdminDao ad = cache.getById(adminId);

            if (ad == null)
                return;

            log.debug("Setting active to " + active + " on cached admin with id: " + adminId);
            ad.setActive(active);

        } catch (DaoException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            log.debug("Deleting cached admin with id: " + adminId);
            cache.deleteById(adminId);

            throw e;
        }
    }
}
