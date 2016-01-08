package k23b.am.cc;

import k23b.am.dao.ResultDao;

/**
 * A thread safe cache for ResultDao objects with an optional maximum size. Lookup is done by result id.
 */
public class ResultCache {

    private LRUHashMap<Long, ResultDao> cacheResultId;

    public ResultCache() {
        this(0);
    }

    public ResultCache(long maxSize) {

        cacheResultId = maxSize > 0 ? new LRUHashMap<Long, ResultDao>(maxSize) : new LRUHashMap<Long, ResultDao>();
    }

    public synchronized void clear() {

        cacheResultId.clear();
    }

    public synchronized void put(ResultDao rd) {

        cacheResultId.put(rd.getJobId(), rd);
    }

    public synchronized ResultDao getById(long resultId) {

        return cacheResultId.get(resultId);
    }

    public synchronized void deleteById(long resultId) {

        cacheResultId.remove(resultId);
    }
}
