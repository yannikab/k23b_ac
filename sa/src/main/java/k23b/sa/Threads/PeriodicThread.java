package k23b.sa.Threads;

import org.apache.log4j.Logger;

/**
 * Implements the functionality for executing periodically a single nmap task in a separate thread with given period
 *
 */
public class PeriodicThread extends Thread {

	private static final Logger log = Logger.getLogger(PeriodicThread.class);

	private Runnable runnable;
	private int period;

	/**
	 * @param id the id of the thread
	 * @param runnable the Runnable Job to run
	 * @param period the period of execution for the thread
	 */
	public PeriodicThread(long id, Runnable runnable, int period) {

		this.runnable = runnable;
		this.period = period;

		setName("Periodic-(" + id + ", " + period + ")");

		start();
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Invoking the run() method of Job. After its completion sleep for a (period - runtime) secs. If the job took too long, continue immediately.
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		log.info(this + ": Running.");

		while (!isInterrupted()) {

			log.info(this + ": Executing runnable " + runnable + ".");

			long start = System.currentTimeMillis();

			runnable.run();

			log.info(this + ": Finished executing runnable " + runnable + ", isInterrupted(): " + isInterrupted());

			long runtime = System.currentTimeMillis() - start;

			if (runtime >= period * 1000) {
				continue;

			} else {
				try {
					Thread.sleep(period * 1000 - runtime);

				} catch (InterruptedException e) {
					log.info(this + ": Interrupted while sleeping.");

					// restore interrupted status since it is cleared after throwing InterruptedException, so that we exit the while loop
					this.interrupt();
				}
			}
		}

		log.info(this + ": Finished.");
	}
}
