package k23b.am.cc;

import k23b.am.dao.RequestDao;

/**
 * A thread safe cache for RequestDao objects with an optional maximum size. Lookup can be done by request id or request hash.
 */
public class RequestCache {

    private LRUHashMap<Long, RequestDao> cacheRequestId;
    private LRUHashMap<String, RequestDao> cacheHash;

    public RequestCache() {
        this(0);
    }

    public RequestCache(long maxSize) {

        if (maxSize > 0) {
            cacheRequestId = new LRUHashMap<Long, RequestDao>(maxSize);
            cacheHash = new LRUHashMap<String, RequestDao>(maxSize);
        } else {
            cacheRequestId = new LRUHashMap<Long, RequestDao>();
            cacheHash = new LRUHashMap<String, RequestDao>();
        }
    }

    public synchronized void clear() {

        cacheRequestId.clear();
        cacheHash.clear();
    }

    public synchronized void put(RequestDao rd) {

        cacheRequestId.put(rd.getRequestId(), rd);
        cacheHash.put(rd.getHash(), rd);
    }

    public synchronized RequestDao getById(long requestId) {

        return cacheRequestId.get(requestId);
    }

    public synchronized RequestDao getByHash(String hash) {

        return cacheHash.get(hash);
    }

    public synchronized void deleteById(long requestId) {

        RequestDao rd = cacheRequestId.remove(requestId);

        if (rd == null)
            return;

        cacheHash.remove(rd.getHash());
    }

    public synchronized void deleteByHash(String hash) {

        RequestDao rd = cacheHash.remove(hash);

        if (rd == null)
            return;

        cacheRequestId.remove(rd.getRequestId());
    }
}
