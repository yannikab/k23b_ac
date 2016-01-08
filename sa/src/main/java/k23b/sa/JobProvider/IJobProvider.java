package k23b.sa.JobProvider;

import k23b.sa.Job;

/**
 * Provides functionality of reception for jobs to be completed that will be replaced by the Aggregator Manager in Part 2
 *
 */
public interface IJobProvider {

    /**
     * @return true if there is Jobs to be provided; false otherwise
     */
    public boolean hasMoreJobs();

    /**
     * @return an array of a random amount of elements of type Job , dictated by the maxNextJobs property
     */
    public Job[] getNextJobs();
}
