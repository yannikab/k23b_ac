package k23b.sa.ExecutorService;

/**
 * Provides necessary methods for a service executing Runnable Objects.
 */
public interface IExecutorService {

	/**
	 * Submitting the runnable for execution
	 * 
	 * @param r a Runnable for execution
	 * @return True after a successful submission; False otherwise
	 */
	boolean submit(Runnable r);
	
	/**
	 * Running tasks are notified to complete and further submissions are prohibited
	 */
	void shutdown();
	
	/**
	 * Wait for the tasks under completion to end. It is used after the {@link #submit(Runnable) submit} method.
	 * 
	 * @throws InterruptedException if interrupted while waiting
	 */
	void awaitTermination() throws InterruptedException;
}
