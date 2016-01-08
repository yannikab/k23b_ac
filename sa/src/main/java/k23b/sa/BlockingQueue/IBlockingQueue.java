package k23b.sa.BlockingQueue;

/**
 * Provides necessary methods for the implementation of a synchronized queue ,with potentially a max size, out of <E> type items 
 *
 * @param <E> item of any type
 */
public interface IBlockingQueue<E> {

	/**
	 * Inserts an element into the queue. If the size of the queue is equal to the potentially max size, then the call waits until a {@link #get() get} call 
	 * 
	 * @param item element to be inserted
	 * @throws InterruptedException if interrupted while waiting
	 */
	public void put(E item) throws InterruptedException;

	/**
	 * Returns and removes the first element of the queue. If there is none, then the call blocks and it waits until a {@link #put(Object) put} call
	 * 
	 * @return the first element of the queue
	 * @throws InterruptedException if interrupted while waiting.
	 */
	public E get() throws InterruptedException;

	/**
	 * Returns a reference to the first element of the queue. It does not remove it
	 * 
	 * @return <E> the first element of the queue
	 */
	public E peek();

	/**
	 * Returns the current size of the queue
	 * 
	 * @return <E> the current size of the queue
	 */
	public int size();

	/**
	 * Returns true if the queue has a maximum size restriction; false otherwise
	 * 
	 * @return true if the queue has a maximum size restriction; false otherwise
	 */
	boolean hasMaxSize();
}
