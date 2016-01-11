package k23b.am.cc;

import k23b.am.dao.UserDao;

/**
 * A thread safe cache for UserDao objects with an optional maximum size. Lookup can be done by id or username.
 */
public class UserCache {

    private LRUHashMap<Long, UserDao> cacheUserId;
    private LRUHashMap<String, UserDao> cacheUsername;

    public UserCache() {
        this(0);
    }

    public UserCache(long maxSize) {

        if (maxSize > 0) {
            cacheUserId = new LRUHashMap<Long, UserDao>(maxSize);
            cacheUsername = new LRUHashMap<String, UserDao>(maxSize);
        } else {
            cacheUserId = new LRUHashMap<Long, UserDao>();
            cacheUsername = new LRUHashMap<String, UserDao>();
        }
    }

    public synchronized void clear() {

        cacheUserId.clear();
        cacheUsername.clear();
    }

    public synchronized void put(UserDao ud) {

        cacheUserId.put(ud.getUserId(), ud);
        cacheUsername.put(ud.getUsername(), ud);
    }

    public synchronized UserDao getById(long adminId) {

        return cacheUserId.get(adminId);
    }

    public synchronized UserDao getByUsername(String username) {

        return cacheUsername.get(username);
    }

    public synchronized void deleteById(long adminId) {

        UserDao ud = cacheUserId.remove(adminId);

        if (ud == null)
            return;

        cacheUsername.remove(ud.getUsername());
    }

    public synchronized void deleteByUsername(String username) {

        UserDao ud = cacheUsername.remove(username);

        if (ud == null)
            return;

        cacheUserId.remove(ud.getUserId());
    }
}
