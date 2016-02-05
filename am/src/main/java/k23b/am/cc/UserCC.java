package k23b.am.cc;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import k23b.am.dao.DaoException;
import k23b.am.dao.UserDao;

/**
 * Caching control layer for user objects.
 */
public class UserCC {

    private static final Logger log = Logger.getLogger(UserCC.class);

    private static volatile UserCache cache = null;

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
            cache = maxSize > 0 ? new UserCache(maxSize) : new UserCache();
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
     * Creates a new user using the data provided and caches it.
     * 
     * @param username the user's username.
     * @param password the user's password.
     * @param active the user's active status.
     * @return the newly created user's id or 0 if the user could not be retrieved for caching after its creation.
     * @throws DaoException if the user could not be created or retrieved for caching.
     */
    public static long create(String username, String password, Date timeRegistered) throws DaoException {

        if (cacheDisabled())
            return UserDao.create(username, password, timeRegistered);

        long userId = UserDao.create(username, password, timeRegistered);

        UserDao ud = UserDao.findById(userId);

        if (ud == null)
            return 0;

        accessesAverted.decrementAndGet();

        cache.put(ud);

        return ud.getUserId();
    }

    /**
     * Retrieves an existing user with specified id and caches it.
     * 
     * @param userId the user's id.
     * @return an object representing the user found, or null if a user with specified id was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static UserDao findById(long userId) throws DaoException {

        if (cacheDisabled())
            return UserDao.findById(userId);

        UserDao ud = cache.getById(userId);

        if (ud != null) {
            accessesAverted.incrementAndGet();
            log.debug(String.format("Returning cached user with id: %d (Data accesses averted: %d)", userId, accessesAverted.get()));
            return ud;
        }

        ud = UserDao.findById(userId);

        if (ud != null)
            cache.put(ud);

        return ud;
    }

    /**
     * Retrieves an existing user with specified username and caches it.
     * 
     * @param username the user's username.
     * @return an object representing the user found, or null if a user with specified username was not found.
     * @throws DaoException if a data access error occurs.
     */
    public static UserDao findByUsername(String username) throws DaoException {

        if (cacheDisabled())
            return UserDao.findByUsername(username);

        UserDao ud = cache.getByUsername(username);

        if (ud != null) {
            accessesAverted.incrementAndGet();
            log.debug(String.format("Returning cached user with username: %s (Data accesses averted: %d)", username, accessesAverted.get()));
            return ud;
        }

        ud = UserDao.findByUsername(username);

        if (ud != null)
            cache.put(ud);

        return ud;
    }

    /**
     * Retrieves all users currently in store and caches them.
     * 
     * @return a set of objects, each representing a user.
     * @throws DaoException if a data access error occurs.
     */
    public static Set<UserDao> findAll() throws DaoException {

        if (cacheDisabled())
            return UserDao.findAll();

        Set<UserDao> users = UserDao.findAll();

        for (UserDao ud : users)
            cache.put(ud);

        return users;
    }

    /**
     * Sets the admin id of a user to a specific value. If the user was cached, updates the cached instance as well.
     * 
     * @param userId the user's id.
     * @param adminId the user's admin id.
     * @throws DaoException if the user's active status could not be changed to the specified value or a data access error occurs.
     */
    public static void setAdminId(long userId, long adminId) throws DaoException {

        if (cacheDisabled()) {
            UserDao.setAdminId(userId, adminId);
            return;
        }

        try {

            UserDao.setAdminId(userId, adminId);

            UserDao ud = cache.getById(userId);

            if (ud == null)
                return;

            log.debug("Setting admin id to " + adminId + " on cached user with id: " + userId);
            ud.setAdminId(adminId);

        } catch (DaoException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            log.debug("Deleting cached user with id: " + userId);
            cache.deleteById(userId);

            throw e;
        }
    }

    /**
     * Sets a user's accepted time to a specific value. If the user was cached, updates the cached instance as well.
     *
     * @param userId the user's id.
     * @param timeAccepted the time user was accepted.
     * @throws DaoException if the user's active status could not be changed to the specified value or a data access error occurs.
     */
    public static void setTimeAccepted(long userId, Date timeAccepted) throws DaoException {

        if (cacheDisabled()) {
            UserDao.setTimeAccepted(userId, timeAccepted);
            return;
        }

        try {

            UserDao.setTimeAccepted(userId, timeAccepted);

            UserDao ud = cache.getById(userId);

            if (ud == null)
                return;

            log.debug("Setting time accepted to " + timeAccepted + " on cached user with id: " + userId);
            ud.setTimeAccepted(timeAccepted);

        } catch (DaoException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            log.debug("Deleting cached user with id: " + userId);
            cache.deleteById(userId);

            throw e;
        }
    }

    /**
     * Sets the active time of a user to a specific value. If the user was cached, updates the cached instance as well.
     *
     * @param userId the user's id.
     * @param timeActive the time user was active.
     * @throws DaoException if the user's active time could not be changed to the specified value or a data access error occurs.
     */
    public static void setTimeActive(long userId, Date timeActive) throws DaoException {

        if (cacheDisabled()) {
            UserDao.setTimeActive(userId, timeActive);
            return;
        }

        try {

            UserDao.setTimeActive(userId, timeActive);

            UserDao ud = cache.getById(userId);

            if (ud == null)
                return;

            log.debug("Setting time active to " + timeActive + " on cached user with id: " + userId);
            ud.setTimeActive(timeActive);

        } catch (DaoException e) {
            // e.printStackTrace();
            log.error(e.getMessage());

            log.debug("Deleting cached user with id: " + userId);
            cache.deleteById(userId);

            throw e;
        }
    }
}
