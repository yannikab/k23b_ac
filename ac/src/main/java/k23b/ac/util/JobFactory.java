package k23b.ac.util;

import k23b.ac.db.dao.CachedJobDao;
import k23b.ac.db.dao.JobDao;
import k23b.ac.rest.Job;

/**
 * A utility class in which a JobDao or a CachedJobDao is converted into a Job object
 *
 */
public class JobFactory extends Job {

    public static Job fromDao(JobDao jd) {

        JobFactory j = new JobFactory();

        j.jobId = jd.getId();
        j.agentId = jd.getAgentId();
        j.timeAssigned = jd.getTime_assigned();
        j.params = jd.getParameters() == null ? "" : jd.getParameters();
        j.periodic = jd.getPeriodic();
        j.period = jd.getPeriod();

        return j;
    }

    public static Job fromCachedDao(CachedJobDao jd) {

        JobFactory j = new JobFactory();

        j.jobId = jd.getJobId();
        j.agentId = jd.getAgentId();
        j.timeAssigned = jd.getTimeAssigned();
        j.timeSent = jd.getTimeSent();
        j.params = jd.getParameters() == null ? "" : jd.getParameters();
        j.periodic = jd.getPeriodic();
        j.period = jd.getPeriod();

        return j;
    }
}
