package k23b.sa.JobProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import k23b.sa.Job;
import k23b.sa.Result;
import k23b.sa.BlockingQueue.IBlockingQueue;

import org.apache.log4j.Logger;

/**
 * It generates a large amount of tasks based on a small file specified by the jobFileName property. Its purpose is mainly for testing
 *
 */
public class JobGenerator implements IJobProvider {

    private static final Logger log = Logger.getLogger(JobGenerator.class);

    private static final int MAXTOTALJOBS = 300;

    private int maxNextJobs;

    private String jobFileName;
    private IBlockingQueue<Result> jobResultsBlockingQueue;

    private Job[] allJobs;
    private int currentJob;

    private Random random = new Random(System.currentTimeMillis());

    public JobGenerator(String jobFileName, int maxNextJobs, IBlockingQueue<Result> jobResultsBlockingQueue) {

        this.jobFileName = jobFileName;
        this.maxNextJobs = maxNextJobs;
        this.jobResultsBlockingQueue = jobResultsBlockingQueue;

        try {
            this.allJobs = generateJobs(random.nextInt(MAXTOTALJOBS));
        } catch (Exception e) {
            log.error("Could not generate any jobs: " + e.getClass().getName());
            this.allJobs = new Job[0];
        }

        this.currentJob = 0;
    }

    @Override
    public boolean hasMoreJobs() {

        return this.currentJob < this.allJobs.length;
    }

    @Override
    public Job[] getNextJobs() {

        int nextJobs = random.nextInt(this.maxNextJobs);

        List<Job> jobs = new ArrayList<Job>();

        for (int i = 0; i < nextJobs && hasMoreJobs(); i++, this.currentJob++)
            jobs.add(this.allJobs[this.currentJob]);

        return jobs.toArray(new Job[0]);
    }

    private Job[] generateJobs(int number) {

        Job[] jobs = new Job[number];

        List<Job> allJobs = readAllJobs();

        int count = allJobs.size();

        for (int i = 0; i < number; i++) {

            Job randomJob = allJobs.get(random.nextInt(count));

            jobs[i] = new Job(i, randomJob.getCmdArray(), randomJob.isPeriodic(), randomJob.getPeriod(), this.jobResultsBlockingQueue);
        }

        return jobs;
    }

    private List<Job> readAllJobs() {

        List<Job> jobs = new ArrayList<Job>();

        File jobFile = new File(this.jobFileName);

        try {

            log.info("Opening file: " + jobFile.toString());

            FileReader fr = new FileReader(jobFile);
            BufferedReader br = new BufferedReader(fr);

            try {

                String line;

                while ((line = br.readLine()) != null) {

                    jobs.add(new LineParser(line, this.jobResultsBlockingQueue).getJob());
                }

            } catch (IOException e) {
                log.error("Error while reading from file: " + jobFile.toString());
            } finally {
                try {

                    log.info("Closing file: " + jobFile.toString());

                    br.close();
                    fr.close();

                } catch (IOException e) {
                    log.error("Error while closing file " + jobFile.toString());
                }
            }

        } catch (FileNotFoundException e) {
            log.error("File not found: " + jobFile.toString());
        }

        return jobs;
    }
}
