package k23b.ac.util;

import k23b.ac.db.dao.JobDao;
import k23b.ac.rest.Job;

public class JobFactory extends Job {

    public static Job fromDao(JobDao jd) {

        JobFactory j = new JobFactory();

        j.jobId = jd.getId();
        j.agentId = jd.getAgentId();
        j.timeAssigned = jd.getTime_assigned();
        j.params = jd.getParameters();
        j.periodic = jd.getPeriodic();
        j.period = jd.getPeriod();

        return j;
    }
}
