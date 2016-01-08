package k23b.sa.Threads;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import k23b.sa.Result;
import k23b.sa.AgentState.JobsState;
import k23b.sa.BlockingQueue.IBlockingQueue;
import k23b.sa.rest.ResultList;

/**
 * Implements the functionality of sending the results that are stored in the blocking queue to the Aggregator manager (on part1 it outputs to stdout)
 *
 */
public class SenderThread extends Thread {

    private static final Logger log = Logger.getLogger(SenderThread.class);

    MainThread mainThread;

    private IBlockingQueue<Result> jobResultsBlockingQueue;

    /**
     * @param period the time interval between result sending. It uses the senderThreadInterval property value
     * @param jobResultsBlockingQueue the shared queue between WorkerThread and PeriodicThred for sending the job results to the Aggregator Manager
     */
    public SenderThread(MainThread mainThread, IBlockingQueue<Result> jobResultsBlockingQueue) {

        this.mainThread = mainThread;

        this.jobResultsBlockingQueue = jobResultsBlockingQueue;

        setName("Sender");

        log.debug(this + ": Starting.");

        start();
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Invokes the {@link #sendResults() sendResults} every period secs
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        log.info(this + ": Running.");

        // Check if it is interrupted after a sleep or before even starting
        while (!isInterrupted()) {

            // only send results if main thread is in jobs processing state
            if (mainThread.getCurrentState() instanceof JobsState)
                sendResults();

            try {
                // If thread has been interrupted, sleep() will immediately throw an InterruptedException
                Thread.sleep(mainThread.getSettings().getSenderThreadInterval() * 1000);

            } catch (InterruptedException e) {
                // Got an interrupt()
                log.info(this + ": Interrupted while sleeping.");

                // set the interrupt flag again so as to break out of loop
                interrupt();
            }
        }

        Thread.interrupted(); // this will clear the interrupted flag so we can get results with queue.get()

        sendResults(); // make one last pass since we were interrupted and the results queue may still have some items

        String message = this + ": Finished.";
        log.info(message);
        System.out.println(System.lineSeparator() + message);
    }

    /**
     * Sends the results that are stored in the blocking queue to the Aggregator manager
     */
    private void sendResults() {
        // Send the current size of the jobResults Queue

        int resultsSize = jobResultsBlockingQueue.size();

        String message = this + ": Results queue size is " + resultsSize + ".";
        // System.out.println(System.lineSeparator() + message);
        System.out.println(message);
        log.info(message);

        if (resultsSize > 0) {

            List<Result> results = new ArrayList<Result>(resultsSize);

            while (jobResultsBlockingQueue.size() > 0)

                try {

                    Result r = jobResultsBlockingQueue.get();

                    results.add(r);

                } catch (InterruptedException e) {

                    log.error(this + ": InterruptedException while getting result from queue.");

                    interrupt(); // because the interrupted has been cleared on exception handling

                    break;
                }

            // output results
            for (Result r : results)
                System.out.println(r.toString());

            // send the results to AM
            ResultList resultList = new ResultList();
            resultList.setResult(results);

            Builder builder = mainThread
                    .getClient()
                    .target(mainThread.getSettings().getBaseURI() + "handle/results/post/" + mainThread.getAgentStats().getHash() + "/")
                    .request(MediaType.APPLICATION_XML);

            try {

                Response response = builder.post(Entity.entity(resultList, MediaType.APPLICATION_XML));
                
                if (response.getStatus() != 200){
                	message = "Sending results failed. HTTP error code: " + response.getStatus();
                    log.error(message);
                    System.out.println(message);
                }
                
                
            } catch (ProcessingException | WebApplicationException e) {

                log.error(e.getMessage());
                System.out.println(e.getMessage());
            }
        }
    }
}
