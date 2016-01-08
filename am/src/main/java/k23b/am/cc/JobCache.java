package k23b.am.cc;

import k23b.am.dao.JobDao;

/**
 * A thread safe cache for JobDao objects with an optional maximum size. Lookup is done by job id.
 */
public class JobCache {

    private LRUHashMap<Long, JobDao> cacheJobId;

    public JobCache() {
        this(0);
    }

    public JobCache(long maxSize) {

        cacheJobId = maxSize > 0 ? new LRUHashMap<Long, JobDao>(maxSize) : new LRUHashMap<Long, JobDao>();
    }

    public synchronized void clear() {

        cacheJobId.clear();
    }

    public synchronized void put(JobDao jd) {

        cacheJobId.put(jd.getJobId(), jd);
    }

    public synchronized JobDao getById(long jobId) {

        return cacheJobId.get(jobId);
    }

    public synchronized void deleteById(long jobId) {

        cacheJobId.remove(jobId);
    }
}
