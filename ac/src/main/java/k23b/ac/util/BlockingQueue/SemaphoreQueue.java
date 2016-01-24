package k23b.ac.util.BlockingQueue;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import android.util.Log;


/**
 * Includes an unsynchronized queue where the synchronized {@link #put(Object) put} and {@link #get() get} are done using two Semaphores.
 *
 * @param <E>
 */
public class SemaphoreQueue<E> implements IBlockingQueue<E> {

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

        Log.d(SemaphoreQueue.class.getName(), ": creating queue.");

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

        	Log.d(SemaphoreQueue.class.getName(), ": setting maximum size to " + maxSize + ".");

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
        	Log.d(SemaphoreQueue.class.getName(), ": put() called from interrupted thread, throwing InterruptedException.");
            throw new InterruptedException();
        }

        if (hasMaxSize())
            available.acquire();

        synchronized (this) {

            queue.add(item);

            Log.d(SemaphoreQueue.class.getName(), ": enqueued item " + item + ".");
        }

        occupied.release();
    }

    @Override
    public E get() throws InterruptedException {

        if (Thread.interrupted()) {
        	Log.d(SemaphoreQueue.class.getName(), ": get() called from interrupted thread, throwing InterruptedException.");
            throw new InterruptedException();
        }

        occupied.acquire();

        E item;

        synchronized (this) {

            item = queue.remove();

            Log.d(SemaphoreQueue.class.getName(), ": dequeued item " + item + ".");
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
