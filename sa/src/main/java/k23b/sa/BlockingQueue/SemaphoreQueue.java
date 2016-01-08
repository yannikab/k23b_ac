package k23b.sa.BlockingQueue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

/**
 * Includes an unsynchronized queue where the synchronized {@link #put(Object) put} and {@link #get() get} are done using two Semaphores.
 *
 * @param <E>
 */
public class SemaphoreQueue<E> implements IBlockingQueue<E> {

	private static final Logger log = Logger.getLogger(SemaphoreQueue.class);

	private Queue<E> queue;

	/**
	 * Reflects the number of occupied slots in the queue
	 */
	private Semaphore occupied;

	/**
	 * Reflects the number of available slots in the queue. It is used only if there is a designated maximum size
	 */
	private Semaphore available;

	private final String name;

	/**
	 * Constructs an empty queue with no maximum capacity
	 * 
	 * @param name the name of the newly constructed queue
	 */
	public SemaphoreQueue(String name) {

		this.name = name;

		log.info(this + ": creating queue.");

		queue = new LinkedList<E>();

		occupied = new Semaphore(0);
		available = null;
	}

	/**
	 * Constructs an empty queue with the specified maximum capacity
	 * 
	 * @param name the name of the newly constructed queue
	 * @param maxSize the maximum capacity of the queue
	 */
	public SemaphoreQueue(String name, int maxSize) {

		this(name);

		if (maxSize > 0) {

			log.info(this + ": setting maximum size to " + maxSize + ".");

			available = new Semaphore(maxSize);
		}
	}

	@Override
	public boolean hasMaxSize() {
		return available != null;
	}

	@Override
	public void put(E item) throws InterruptedException {

		if (Thread.interrupted()) {
			log.debug(this + ": put() called from interrupted thread, throwing InterruptedException.");
			throw new InterruptedException();
		}

		if (hasMaxSize())
			available.acquire();

		synchronized (this) {

			queue.add(item);

			log.debug(this + ": enqueued item " + item + ".");
		}

		occupied.release();
	}

	@Override
	public E get() throws InterruptedException {

		if (Thread.interrupted()) {
			log.debug(this + ": get() called from interrupted thread, throwing InterruptedException.");
			throw new InterruptedException();
		}

		occupied.acquire();

		E item;

		synchronized (this) {

			item = queue.remove();

			log.debug(this + ": dequeued item " + item + ".");
		}

		if (hasMaxSize())
			available.release();

		return item;
	}

	@Override
	public synchronized E peek() {
		return queue.peek();
	}

	@Override
	public synchronized int size() {
		return queue.size();
	}

	@Override
	public String toString() {
		return name;
	}
}
