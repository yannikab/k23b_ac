package k23b.am.rest;

import k23b.am.Settings;
import k23b.am.dao.AgentDao;
import k23b.am.srv.SrvException;

public class AgentFactory extends Agent {

    public static Agent fromDao(AgentDao ad) throws SrvException {

        Agent a = new Agent();

        a.agentId = ad.getAgentId();
        a.timeAccepted = ad.getTimeAccepted();
        a.timeJobRequest = ad.getTimeJobRequest();
        a.timeTerminated = ad.getTimeTerminated();
        a.agentStatus = a.getStatus(Settings.getJobRequestInterval());

        return a;
    }
}
