package k23b.ac.util;

import java.util.Date;

import k23b.ac.dao.JobDao;
import k23b.ac.rest.Job;

public class JobDaoFactory extends JobDao {

    protected JobDaoFactory(long id, String parameters, String username, long agentId, Date time_assigned, boolean periodic, int period) {

        super(id, parameters, username, agentId, time_assigned, periodic, period);
    }

    public static JobDao fromJob(Job j) {

        return new JobDaoFactory(j.getJobId(), j.getParams(), null, j.getAgentId(), j.getTimeAssigned(), j.getPeriodic(), j.getPeriod());
    }
}
