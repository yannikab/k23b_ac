package k23b.sa.JobProvider;

import java.util.ArrayList;
import java.util.List;

import k23b.sa.Job;
import k23b.sa.Result;
import k23b.sa.BlockingQueue.IBlockingQueue;
import k23b.sa.rest.JobContainer;

/**
 * Creates an array of Jobs sent by the AM.
 */

public class RestJobProvider implements IJobProvider {

    private IBlockingQueue<Result> jobResultsBlockingQueue;
    private List<String> jobList;

    public RestJobProvider(JobContainer jl, IBlockingQueue<Result> jobResultsBlockingQueue) {
        this.jobResultsBlockingQueue = jobResultsBlockingQueue;
        this.jobList = jobListToStringList(jl);
    }

    @Override
    public boolean hasMoreJobs() {
        return (!jobList.isEmpty());
    }

    @Override
    public Job[] getNextJobs() {

        List<Job> jList = new ArrayList<Job>();

        while (hasMoreJobs())
            jList.add(new LineParser(jobList.remove(0), this.jobResultsBlockingQueue).getJob());

        return jList.toArray(new Job[0]);
    }

    private List<String> jobListToStringList(JobContainer jl) {

        List<String> jobList = new ArrayList<String>();

        for (Job c : jl.getJobs()) {

            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(c.getJobId()));
            sb.append(",");
            sb.append(c.getCmd());
            sb.append(",");
            sb.append(String.valueOf(c.getPeriodic()));
            sb.append(",");
            sb.append(c.getPeriod());

            jobList.add(sb.toString());
        }

        return jobList;
    }
}
