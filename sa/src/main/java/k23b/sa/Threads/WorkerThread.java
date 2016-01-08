package k23b.sa.Threads;

import k23b.sa.BlockingQueue.IBlockingQueue;

import org.apache.log4j.Logger;

/**
 * Implements the functionality for executing a single nmap task in a separate thread
 *
 */

public class WorkerThread extends Thread {

    private static final Logger log = Logger.getLogger(WorkerThread.class);

    private IBlockingQueue<Runnable> runnableBlockingQueue;

    /**
     * @param id the id of the thread
     * @param runnableBlockingQueue the synchronized queue of Runnable Jobs
     */
    public WorkerThread(int id, IBlockingQueue<Runnable> runnableBlockingQueue) {

        this.runnableBlockingQueue = runnableBlockingQueue;

        setName("Worker-" + (char) (id + 65));

        start();
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Invoking the run() method of Job.
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        log.info(this + ": Running.");

        while (!this.isInterrupted()) {

            try {

                Runnable r = runnableBlockingQueue.get();

                log.info(this + ": Got runnable " + r + " from queue.");

                r.run();

                log.info(this + ": Finished executing runnable " + r + ", isInterrupted(): " + isInterrupted());

            } catch (InterruptedException e) {

                log.info(this + ": Interrupted while waiting to get a runnable.");

                // restore interrupted status since it is cleared after throwing InterruptedException, so that we exit the while loop
                this.interrupt();
            }
        }

        log.info(this + ": Finished.");
    }
}
