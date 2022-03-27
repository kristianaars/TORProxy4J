package utils;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingBuffer<T> {

    private final ReentrantLock bufferLock;
    private final Condition bufferCondition;

    private final LinkedList<T> buffer;

    public BlockingBuffer() {
        super();

        buffer = new LinkedList<>();
        bufferLock = new ReentrantLock();
        bufferCondition = bufferLock.newCondition();
    }

    /**
     * Insert item into buffer, will wake up any waiters.
     *
     * @param i Item to insert
     */
    public void insert(T i) {
        bufferLock.lock();
        buffer.add(i);
        bufferCondition.signalAll();
        bufferLock.unlock();
    }

    /**
     * Poll item from queue. If there are no items, the method will wait until a new item arrives.
     *
     * @return Next item from queue
     */
    public T poll() {
        bufferLock.lock();

        while(this.isEmpty()) {
            try {
                bufferCondition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        T b = buffer.poll();
        bufferLock.unlock();

        return b;
    }

    public boolean isEmpty() {
        bufferLock.lock();
        boolean b = buffer.isEmpty();
        bufferLock.unlock();
        return b;
    }

    public int getSize() {
        bufferLock.lock();
        int size = buffer.size();
        bufferLock.unlock();
        return size;
    }
}
