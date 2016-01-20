package k23b.am.rest;

import k23b.am.dao.JobDao;

public class JobFactory extends Job {

    public static Job fromDao(JobDao jd) {

        Job j = new Job();

        j.jobId = jd.getJobId();
        j.agentId = jd.getAgentId();
        j.adminId = jd.getAdminId();
        j.timeAssigned = jd.getTimeAssigned();
        j.timeSent = jd.getTimeSent();
        j.params = jd.getParams();
        j.periodic = jd.getPeriodic();
        j.period = jd.getPeriod();
        j.timeStopped = jd.getTimeStopped();

        return j;
    }
}
