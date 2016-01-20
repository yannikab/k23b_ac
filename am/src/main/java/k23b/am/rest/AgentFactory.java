package k23b.am.rest;

import k23b.am.dao.AgentDao;
import k23b.am.srv.AdminSrv;
import k23b.am.srv.RequestSrv;
import k23b.am.srv.SrvException;

public class AgentFactory extends Agent {

    public static Agent fromDao(AgentDao ad) throws SrvException {
      
        Agent a = new Agent();

        a.agentId = ad.getAgentId();
        a.requestHash = RequestSrv.findById(ad.getRequestId()).getHash();
        a.adminUsername = AdminSrv.findById(ad.getAdminId()).getUsername();
        a.timeAccepted = ad.getTimeAccepted();
        a.timeJobRequest = ad.getTimeJobRequest();
        a.timeTerminated = ad.getTimeTerminated();
        
        return a;
    }
}