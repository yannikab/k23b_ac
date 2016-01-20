package k23b.sa.AgentState;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import k23b.sa.AgentStats;
import k23b.sa.Job;
import k23b.sa.RequestStatus;
import k23b.sa.Result;
import k23b.sa.BlockingQueue.IBlockingQueue;
import k23b.sa.BlockingQueue.WaitNotifyQueue;
import k23b.sa.ExecutorService.IExecutorService;
import k23b.sa.ExecutorService.ThreadPool;
import k23b.sa.JobProvider.IJobProvider;
import k23b.sa.JobProvider.RestJobProvider;
import k23b.sa.Threads.MainThread;
import k23b.sa.Threads.PeriodicThread;
import k23b.sa.Threads.SenderThread;
import k23b.sa.Threads.ShutdownThread;
import k23b.sa.rest.JobContainer;

/**
 * The State in which the SA gets a batch of Jobs from the AM.
 */
public class JobsState extends AgentState {

    private static final Logger log = Logger.getLogger(JobsState.class);

    private static boolean initialized = false;

    private static IExecutorService executorService;
    private static Map<Long, Thread> periodicThreadMap;
    private static IBlockingQueue<Result> resultsBlockingQueue;

    public JobsState(MainThread mainThread) {
        super(mainThread);

        if (initialized)
            return;

        initialized = true;

        // periodic threads Hash Map
        periodicThreadMap = new HashMap<Long, Thread>();

        // thread pool
        executorService = new ThreadPool("threadPool", getMainThread().getSettings().getThreadPoolSize());

        // The shared queue of the job results
        resultsBlockingQueue = new WaitNotifyQueue<Result>("resultsBlockingQueue", getMainThread().getSettings().getResultsQueueMax());

        // Start the SenderThread, begin to periodically send results to AM
        Thread senderThread = new SenderThread(getMainThread(), resultsBlockingQueue);

        // register shutdown hook
        Runtime.getRuntime().addShutdownHook(new ShutdownThread(getMainThread(), periodicThreadMap, executorService, senderThread));
    }

    @Override
    public void handleState() {

        try {

            Thread.sleep(getMainThread().getSettings().getJobRequestInterval() * 1000);

        } catch (InterruptedException e) {

            log.info(this + ": Interrupted while sleeping.");

            Thread.currentThread().interrupt();
        }

        JobContainer jobList = getJobs();

        RequestStatus rs = requestStatus(jobList);

        String msg = "Status received: " + rs.toString();
        log.info(msg);
        System.out.println(msg);

        switch (rs) {

        case ACCEPTED: {

            if (jobList.getJobs().isEmpty()) {

                String message = "Aggregator Manager sent an empty job list.";
                System.out.println(message);
                log.info(message);

                break;
            }

            // Job provider will be fed a list of Jobs by the AM.
            IJobProvider jobProvider = new RestJobProvider(jobList, resultsBlockingQueue);

            // Distribute the jobs into Threads
            Job[] jobsBatch = jobProvider.getNextJobs();

            String message = Thread.currentThread().getName() + ": Received " + jobsBatch.length + " jobs.";
            log.info(message);
            System.out.println(message);

            for (Job job : jobsBatch) {

                if (getMainThread().isInterrupted()) {
                    setCurrentState(new ShutdownState(getMainThread()));
                    return;
                }

                System.out.println(job.description());

                if (job.isTerminating()) {

                    setCurrentState(new ShutdownState(getMainThread()));
                    return;

                } else if (job.isPeriodicStop()) {

                    try {

                        long jobId = Long.parseLong(job.getCmdArray()[1]);

                        Thread pt = periodicThreadMap.remove(jobId);

                        if (pt != null) {

                            log.info("Stopping periodic job with id: " + jobId);
                            pt.interrupt();
                            pt.join();

                            String ptMessage = Thread.currentThread().getName() + ": Periodic thread with JobId: " + jobId + " terminated.";
                            System.out.println(System.lineSeparator() + ptMessage);
                            log.info(ptMessage);
                        }

                    } catch (NumberFormatException e) {
                        // e.printStackTrace();
                        log.error("Aggregator Manager sent an invalid periodic stop job.");
                    } catch (InterruptedException e) {
                        // e.printStackTrace();
                        log.error(Thread.currentThread().getName() + ": InterruptedException during periodic thread join().");
                    }

                } else if (job.getPeriodic()) {

                    periodicThreadMap.put(job.getJobId(), new PeriodicThread(job.getJobId(), job, job.getPeriod()));

                } else {

                    executorService.submit(job);
                }
            }

            break;
        }

        case PENDING:
            setCurrentState(new CheckState(getMainThread()));
            return;

        case REJECTED:
            setCurrentState(new ShutdownState(getMainThread()));
            return;

        case UNKNOWN:
            setCurrentState(new RegisterState(getMainThread()));
            return;

        case INVALID:
            setCurrentState(new RegisterState(getMainThread()));
            return;
        }
    }

    /**
     * Requests a batch of Jobs from the AM using RESTful communication.
     * 
     * @return A list of Jobs to be run.
     */
    private JobContainer getJobs() {

        // return invalidJobList();

        String message = "Getting jobs from Aggregator Manager.";
        log.info(message);
        System.out.println(message);

        String baseURI = getMainThread().getSettings().getBaseURI();

        AgentStats agentStats = getMainThread().getAgentStats();

        Builder builder = getMainThread()
                .getClient()
                .target(baseURI + "handle/jobs/get/" + agentStats.getHash() + "/")
                .request(MediaType.APPLICATION_XML);

        try {

            Response response = builder.get();

            if (response.getStatus() != 200) {

                log.error("Job request failed. HTTP error code: " + response.getStatus());

                return invalidJobList();
            }

            return response.readEntity(JobContainer.class);

        } catch (ProcessingException e) {
            // e.printStackTrace();
            log.error(e.getMessage());
            System.out.println(e.getMessage());

            return invalidJobList();
        }
    }

    /**
     * @return A Job list with its Status set to Invalid
     */

    private JobContainer invalidJobList() {
        JobContainer jl = new JobContainer("Invalid");
        return jl;
    }

    /**
     * 
     * @param jobList
     * @return The status of the jobList
     */

    private RequestStatus requestStatus(JobContainer jobList) {

        try {

            return RequestStatus.valueOf(jobList.getStatus().toUpperCase());

        } catch (IllegalArgumentException e) {
            // e.printStackTrace();

            String message = "Invalid Request Status: " + jobList.getStatus();
            log.error(message);
            System.out.println(message);

            return RequestStatus.INVALID;
        }
    }
}
