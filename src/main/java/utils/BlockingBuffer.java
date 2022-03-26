package utils;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingBuffer<T> extends LinkedList<T> {

    private final ReentrantLock bufferLock;
    private final Condition bufferCondition;

    public BlockingBuffer() {
        super();

        bufferLock = new ReentrantLock();
        bufferCondition = bufferLock.newCondition();
    }

    public void insert(T i) {
        bufferLock.lock();
        this.add(i);
        bufferCondition.signalAll();
        bufferLock.unlock();
    }

    public T poll() {
        bufferLock.lock();

        while(this.isEmpty()) {
            try {
                bufferCondition.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        T b = super.poll();
        bufferLock.unlock();

        return b;
    }

    @Override
    public boolean isEmpty() {
        bufferLock.lock();
        boolean b = super.isEmpty();
        bufferLock.unlock();
        return b;
    }
}
