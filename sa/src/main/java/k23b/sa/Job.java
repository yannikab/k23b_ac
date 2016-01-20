package k23b.sa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.log4j.Logger;

import k23b.sa.BlockingQueue.IBlockingQueue;
import k23b.sa.rest.DateAdapter;

/**
 * Holds information about an nmap task including its output. As it implements the interface Runnable its method {@link #run() run} is invoked by the WorkerThread to execute the nmap task
 *
 */

@XmlRootElement(name = "job")
@XmlAccessorType(XmlAccessType.FIELD)
public class Job implements Runnable {

    private static final Logger log = Logger.getLogger(Job.class);

    @XmlElement(required = true)
    private long jobId;

    @XmlElement(required = true)
    private long agentId;

    @XmlElement(required = true)
    private long adminId;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = true)
    private Date timeAssigned;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = false)
    private Date timeSent;

    @XmlElement(required = true)
    private String params;

    @XmlElement(required = true)
    private boolean periodic;

    @XmlElement(required = false)
    private int period;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(required = false)
    private Date timeStopped;

    @XmlTransient
    private IBlockingQueue<Result> jobResultsBlockingQueue;

    @XmlTransient
    private String[] cmdArray;

    @XmlTransient
    private String output;

    public Job() {
        super();
    }

    public Job(int id, String[] cmdArray, boolean isPeriodic, int period, IBlockingQueue<Result> jobResultsBlockingQueue) {

        this.jobId = id;
        this.cmdArray = cmdArray;
        this.periodic = isPeriodic;
        this.period = period;
        this.jobResultsBlockingQueue = jobResultsBlockingQueue;

        this.output = null;
    }

    public long getJobId() {
        return jobId;
    }

    public String[] getCmdArray() {
        return cmdArray;
    }

    public boolean getPeriodic() {
        return periodic;
    }

    public int getPeriod() {
        return period;
    }

    public String getCmd() {
        return params;
    }

    public String getOutput() {
        return output;
    }

    public boolean isTerminating() {

        return (cmdArray[0].compareTo("exit") == 0);
    }

    public boolean isPeriodicStop() {

        return (cmdArray[0].compareTo("stop") == 0);
    }

    /**
     * This method is invoked by the WorkerThread or PeriodicThread to execute the nmap task
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

        try {

            long start = System.currentTimeMillis();

            log.info("Starting job: " + this.description());

            Process proc = Runtime.getRuntime().exec(this.cmdArray);
            InputStream stderr = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);

            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);

            String line = null;

            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }

            bw.flush();

            this.output = sw.toString();

            log.debug("Job " + this.jobId + ": output taken." + " isInterrupted(): " + Thread.currentThread().isInterrupted());

            // Store current interrupt status and clear it, prevent waitFor() and put() from throwing InterruptedException
            boolean isInterrupted = Thread.interrupted();

            int exitVal = proc.waitFor();

            long end = System.currentTimeMillis();

            log.debug("Job " + this.jobId + " exit code: " + exitVal + ", run time: " + (end - start) + " isInterrupted(): " + Thread.currentThread().isInterrupted());

            switch (exitVal) {
            case 0: // nmap finished normally

                log.info("Job finished successfully: " + this.description());

                // put the XML output into the jobResults Queue
                jobResultsBlockingQueue.put(new Result(this.jobId, output));

                log.debug("Job " + this.jobId + ": Result placed on queue.");

                break;
            case 1: // nmap reports network error

                log.error("Network error from job: " + this.description());

                jobResultsBlockingQueue.put(new Result(this.jobId, null));

                log.debug("Job " + this.jobId + ": Result placed on queue.");

                break;
            case 2: // nmap reports unknown error

                log.error("Error from job: " + this.description());

                jobResultsBlockingQueue.put(new Result(this.jobId, null));

                log.debug("Job " + this.jobId + ": Result placed on queue.");

                break;
            default: // invalid exit code from nmap, we assume thread was interrupted while running process

                log.info("Job " + this.jobId + ": Invalid exit code from nmap, job was interrupted.");

                isInterrupted = true;
            }

            // if the thread was interrupted while running the process and because we have cleared the interrupted status,
            // throw an InterruptedException so that the thread that runs this gets notified
            if (isInterrupted)
                throw new InterruptedException();

        } catch (InterruptedException e) { // proc.waitFor(), blockingQueue.put()

            log.info("InterruptedException while running job: " + this.description());

            // restore interrupted status since it is cleared after throwing InterruptedException
            // upon returning, the thread that runs this can detect that an interrupt occurred
            Thread.currentThread().interrupt();

            return;

        } catch (IOException e) { // runtime.exec(), reader.read(), writer.write(), writer.flush()

            log.error("IOException while running job: " + this.description());

            return;
        }
    }

    public String description() {

        StringBuilder sb = new StringBuilder();

        sb.append("Job: ");
        sb.append(this.jobId);
        sb.append(",");

        for (String param : this.cmdArray) {
            sb.append(" ");
            sb.append(param);
        }
        sb.append(", ");

        sb.append(this.periodic ? "true" : "false");

        if (this.periodic) {
            sb.append(", ");
            sb.append(this.period);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return (String.valueOf(jobId));
    }
}
