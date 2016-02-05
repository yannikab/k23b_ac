package k23b.ac.util;

import k23b.ac.db.dao.CachedAgentDao;
import k23b.ac.rest.Agent;
import k23b.ac.rest.status.AgentStatus;

public class AgentFactory extends Agent {

    public static Agent fromCachedDao(CachedAgentDao ad) {

        AgentFactory a = new AgentFactory();

        a.agentId = ad.getAgentId();
        a.requestHash = ad.getAgentHash();
        a.timeAccepted = ad.getTimeAccepted();
        a.timeJobRequest = ad.getTimeJobRequest();
        a.timeTerminated = ad.getTimeTerminated();
        a.agentStatus = AgentStatus.values()[ad.getAgentStatus().ordinal()];

        return a;
    }
}
