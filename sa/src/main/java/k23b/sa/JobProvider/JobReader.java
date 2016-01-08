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
 * It reads from a large file specified by the jobFileName property returning each and every job in the file gradually.
 *
 */
public class JobReader implements IJobProvider {

    private static final Logger log = Logger.getLogger(JobReader.class);

    private int maxNextJobs;

    private static final Random random = new Random(System.currentTimeMillis());

    private String jobFileName;
    private File jobFile;

    private BufferedReader br;

    private boolean isFileOpen;

    private IBlockingQueue<Result> jobResultsBlockingQueue;

    public JobReader(String jobFileName, int maxNextJobs, IBlockingQueue<Result> jobResultsBlockingQueue) {

        this.jobFileName = jobFileName;
        this.maxNextJobs = maxNextJobs;
        this.jobResultsBlockingQueue = jobResultsBlockingQueue;

        this.isFileOpen = false;

        openFile();
    }

    @Override
    public boolean hasMoreJobs() {
        return this.isFileOpen;
    }

    @Override
    public Job[] getNextJobs() {

        int nextJobs = random.nextInt(this.maxNextJobs);

        List<Job> jobList = new ArrayList<Job>();

        try {

            for (int i = 0; i < nextJobs && hasMoreJobs(); i++) {

                String line = br.readLine();

                if (line == null) {
                    closeFile();
                    break;

                } else {

                    jobList.add(new LineParser(line, this.jobResultsBlockingQueue).getJob());
                }
            }
        } catch (IOException e) {
            log.error("Error while reading from file: " + jobFile.toString());
        }

        return jobList.toArray(new Job[0]);
    }

    private void openFile() {
        this.jobFile = new File(this.jobFileName);

        try {

            FileReader fr = new FileReader(this.jobFile);
            this.br = new BufferedReader(fr);

            this.isFileOpen = true;

        } catch (FileNotFoundException e) {
            log.error("File not found: " + this.jobFile.toString());
        }
    }

    private void closeFile() {
        try {

            log.info("Closing file: " + this.jobFile.toString());

            this.br.close();

        } catch (IOException e) {
            log.error("Error while closing file: " + this.jobFile.toString());
        } finally {
            this.isFileOpen = false;
        }
    }
}
