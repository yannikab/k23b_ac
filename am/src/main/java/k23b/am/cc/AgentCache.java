package k23b.am.cc;

import k23b.am.dao.AgentDao;

/**
 * A thread safe cache for AgentDao objects with an optional maximum size. Lookup can be done by agent id or agent request id.
 */
public class AgentCache {

    private LRUHashMap<Long, AgentDao> cacheAgentId;
    private LRUHashMap<Long, AgentDao> cacheRequestId;

    public AgentCache() {
        this(0);
    }

    public AgentCache(long maxSize) {

        if (maxSize > 0) {
            cacheAgentId = new LRUHashMap<Long, AgentDao>(maxSize);
            cacheRequestId = new LRUHashMap<Long, AgentDao>(maxSize);
        } else {
            cacheAgentId = new LRUHashMap<Long, AgentDao>();
            cacheRequestId = new LRUHashMap<Long, AgentDao>();
        }
    }

    public synchronized void clear() {

        cacheAgentId.clear();
        cacheRequestId.clear();
    }

    public synchronized void put(AgentDao ad) {

        cacheAgentId.put(ad.getAgentId(), ad);
        cacheRequestId.put(ad.getRequestId(), ad);
    }

    public synchronized AgentDao getById(long agentId) {

        return cacheAgentId.get(agentId);
    }

    public synchronized AgentDao getByRequestId(long requestId) {

        return cacheRequestId.get(requestId);
    }

    public synchronized void deleteById(long agentId) {

        AgentDao ad = cacheAgentId.remove(agentId);

        if (ad == null)
            return;

        cacheRequestId.remove(ad.getRequestId());
    }

    public synchronized void deleteByRequestId(long requestId) {

        AgentDao ad = cacheRequestId.remove(requestId);

        if (ad == null)
            return;

        cacheAgentId.remove(ad.getAgentId());
    }
}
