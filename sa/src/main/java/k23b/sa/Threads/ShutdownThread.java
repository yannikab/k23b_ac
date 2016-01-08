package k23b.sa.Threads;

import java.util.Map;

import k23b.sa.ExecutorService.IExecutorService;

import org.apache.log4j.Logger;

/**
 * Initializes the addShutdownHook call of Runtime. Provides the smooth shutting down of the application
 *
 */
public class ShutdownThread extends Thread {

    private static final Logger log = Logger.getLogger(ShutdownThread.class);

    private Thread mainThread;
    private Map<Long, Thread> periodicThreadMap;
    private Thread senderThread;
    private IExecutorService workerThreadPool;

    /**
     * @param mainThread the thread of the App
     * @param periodicThreadList a list of the started PeriodicThreads
     * @param executorService the ThreadPool
     * @param senderThread the SenderThread
     */
    public ShutdownThread(Thread mainThread, Map<Long, Thread> periodicThreadMap, IExecutorService executorService, Thread senderThread) {
        this.mainThread = mainThread;
        this.periodicThreadMap = periodicThreadMap;
        this.senderThread = senderThread;
        this.workerThreadPool = executorService;

        setName("Shutdown");
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Interrupting the PeriodicThreads, ThreadPool and SenderThread and waiting for them to end in the aforementioned order
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {

        log.info(this + ": Shutting down...");
        System.out.print(System.lineSeparator() + this + ": Shutting down Software Agent." + System.lineSeparator());

        // main thread termination

        mainThread.interrupt();

        try {

            mainThread.join();

        } catch (InterruptedException e) {
            log.error(this + ": InterruptedException on main thread join().");
        }

        log.info(this + ": main thread finished.");

        // periodic threads termination
        for (Map.Entry<Long, Thread> entry : periodicThreadMap.entrySet())
            entry.getValue().interrupt();

        for (Map.Entry<Long, Thread> entry : periodicThreadMap.entrySet()) {
            try {

                entry.getValue().join();

            } catch (InterruptedException e) {
                log.error(this + ": InterruptedException during periodic thread join().");
            }
        }

        String message = this + ": " + periodicThreadMap.size() + " periodic threads terminated.";
        System.out.println(System.lineSeparator() + message);
        log.info(message);

        // threadpool termination

        workerThreadPool.shutdown();

        try {

            workerThreadPool.awaitTermination();

            message = this + ": Thread pool terminated.";
            System.out.println(System.lineSeparator() + message);
            log.info(message);

        } catch (InterruptedException e) {
            log.error(this + ": InterruptedException during executor service awaitTermination().");
        }

        // sender thread interruption

        senderThread.interrupt();

        try {

            senderThread.join();

            log.info(this + ": sender thread finished.");

        } catch (InterruptedException e) {
            log.error(this + ": InterruptedException during sender thread join().");
        }
    }
}
