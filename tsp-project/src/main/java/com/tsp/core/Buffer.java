package com.tsp.core;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import com.tsp.model.Task;

public class Buffer {
    private final LinkedList<Task> items = new LinkedList<>();
    private final ReentrantLock lock = new ReentrantLock(true);
    private final Condition notEmpty = lock.newCondition();

    public void put(Task task) throws InterruptedException {
        lock.lock();
        try {
            items.addLast(task);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public Task take() throws InterruptedException {
        lock.lock();
        try {
            while (items.isEmpty()) {
                notEmpty.await();
            }
            return items.removeFirst();
        } finally {
            lock.unlock();
        }
    }
}
