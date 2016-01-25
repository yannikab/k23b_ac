package k23b.ac.util.BlockingQueue;

import java.util.LinkedList;
import java.util.Queue;

import android.util.Log;

/**
 * Includes an unsynchronized queue where the synchronized {@link #put(Object) put} and {@link #get() get} are done using {@link #wait() wait} and {@link #notify() notify} calls
 *
 * @param <E>
 */
public class WaitNotifyQueue<E> implements IBlockingQueue<E> {

    private Queue<E> queue;

    private int maxSize;

    private final String name;

    /**
     * Constructs an empty queue with no maximum capacity
     * 
     * @param name the name of the newly constructed queue
     */
    public WaitNotifyQueue(String name) {

        this.name = name;

        Log.d(WaitNotifyQueue.class.getName(), ": creating queue.");

        queue = new LinkedList<E>();

        maxSize = 0;
    }

    /**
     * Constructs an empty queue with the specified maximum capacity
     * 
     * @param name the name of the newly constructed queue
     * @param maxSize the maximum capacity of the queue
     */
    public WaitNotifyQueue(String name, int maxSize) {

        this(name);

        if (maxSize > 0) {

            Log.d(WaitNotifyQueue.class.getName(), ": setting maximum size to " + maxSize + ".");

            this.maxSize = maxSize;
        }
    }

    @Override
    public boolean hasMaxSize() {
        return maxSize > 0;
    }

    @Override
    public void put(E item) throws InterruptedException {

        if (Thread.interrupted()) {
            Log.d(WaitNotifyQueue.class.getName(),
                    ": put() called from interrupted thread, throwing InterruptedException.");
            throw new InterruptedException();
        }

        synchronized (this) {

            if (hasMaxSize()) {
                while (queue.size() == maxSize)
                    this.wait();
            }

            queue.add(item);

            Log.d(WaitNotifyQueue.class.getName(), ": enqueued item " + item + ".");

            this.notifyAll();
        }
    }

    @Override
    public E get() throws InterruptedException {

        if (Thread.interrupted()) {
            Log.d(WaitNotifyQueue.class.getName(),
                    ": get() called from interrupted thread, throwing InterruptedException.");
            throw new InterruptedException();
        }

        E item;

        synchronized (this) {

            while (queue.size() == 0) {
                this.wait();
            }

            item = queue.remove();

            Log.d(WaitNotifyQueue.class.getName(), ": dequeued item " + item + ".");

            if (hasMaxSize()) {
                this.notifyAll();
            }
        }

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
        return this.name;
    }
}
