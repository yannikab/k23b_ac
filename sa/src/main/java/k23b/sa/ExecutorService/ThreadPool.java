package k23b.sa.ExecutorService;

import k23b.sa.BlockingQueue.IBlockingQueue;
import k23b.sa.BlockingQueue.SemaphoreQueue;
import k23b.sa.Threads.WorkerThread;

import org.apache.log4j.Logger;

/**
 * ThreadPool uses a set of {@link WorkerThread} the number of which is set on the class constructor. It holds a {@link IBlockingQueue} from which the threads extract Runnables and run them
 *
 */

public class ThreadPool implements IExecutorService {

	private static final Logger log = Logger.getLogger(ThreadPool.class);

	private IBlockingQueue<Runnable> threadPoolBlockingQueue;

	private WorkerThread[] workerThreads;

	private volatile boolean threadsRunning;

	private volatile boolean isShutDown;

	private String name;

	/**
	 * @param name the name of the thread pool
	 * @param size the amount of WorkerThreads in the thread pool
	 */
	public ThreadPool(String name, int size) {

		this.name = name;

		log.info(this + ": Creating " + size + " worker threads.");

		threadPoolBlockingQueue = new SemaphoreQueue<Runnable>("threadPoolBlockingQueue");

		workerThreads = new WorkerThread[size];

		for (int i = 0; i < workerThreads.length; i++) {
			workerThreads[i] = new WorkerThread(i, threadPoolBlockingQueue);
		}

		log.info(this + ": Created " + size + " worker threads.");

		threadsRunning = true;
		isShutDown = false;
	}

	/**
	 * Submitting the runnable for execution
	 * 
	 * @param r a Runnable for execution
	 * @return True after a successful submission; False otherwise
	 */

	@Override
	public synchronized boolean submit(Runnable r) {

		if (isShutDown)
			return false;

		try {

			threadPoolBlockingQueue.put(r);

			return true;

		} catch (InterruptedException e) {

			log.info(this + ": Interrupted while putting item in queue.");

			Thread.currentThread().interrupt();

			return false;
		}
	}

	/**
	 * Running tasks are notified to complete and further submissions are prohibited
	 */
	@Override
	public synchronized void shutdown() {

		if (threadsRunning) {

			log.info(this + ": Shutting down worker threads.");

			for (WorkerThread t : workerThreads)
				t.interrupt();

			isShutDown = true;
		}
	}

	/**
	 * Wait for the tasks under completion to end. It is used after the {@link #submit(Runnable) submit} method.
	 * 
	 * @throws InterruptedException if interrupted while waiting
	 */
	@Override
	public synchronized void awaitTermination() throws InterruptedException {

		log.info(this + ": Awaiting thread termination.");

		for (WorkerThread t : workerThreads)
			t.join();

		threadsRunning = false;

		log.info(this + ": All worker threads finished.");
	}

	@Override
	public String toString() {
		return this.name;
	}
}
