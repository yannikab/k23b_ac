package k23b.am.cc;

import k23b.am.dao.AdminDao;

/**
 * A thread safe cache for AdminDao objects with an optional maximum size. Lookup can be done by admin id or admin username.
 */
public class AdminCache {

    private LRUHashMap<Long, AdminDao> cacheAdminId;
    private LRUHashMap<String, AdminDao> cacheUsername;

    public AdminCache() {
        this(0);
    }

    public AdminCache(long maxSize) {

        if (maxSize > 0) {
            cacheAdminId = new LRUHashMap<Long, AdminDao>(maxSize);
            cacheUsername = new LRUHashMap<String, AdminDao>(maxSize);
        } else {
            cacheAdminId = new LRUHashMap<Long, AdminDao>();
            cacheUsername = new LRUHashMap<String, AdminDao>();
        }
    }

    public synchronized void clear() {

        cacheAdminId.clear();
        cacheUsername.clear();
    }

    public synchronized void put(AdminDao ad) {

        cacheAdminId.put(ad.getAdminId(), ad);
        cacheUsername.put(ad.getUsername(), ad);
    }

    public synchronized AdminDao getById(long adminId) {

        return cacheAdminId.get(adminId);
    }

    public synchronized AdminDao getByUsername(String username) {

        return cacheUsername.get(username);
    }

    public synchronized void deleteById(long adminId) {

        AdminDao ad = cacheAdminId.remove(adminId);

        if (ad == null)
            return;

        cacheUsername.remove(ad.getUsername());
    }

    public synchronized void deleteByUsername(String username) {

        AdminDao ad = cacheUsername.remove(username);

        if (ad == null)
            return;

        cacheAdminId.remove(ad.getAdminId());
    }
}
