package com.tsp.core;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.tsp.model.Task;

public class Buffer {
    private final LinkedList<Task> items = new LinkedList<>();
    private final ReentrantLock lock = new ReentrantLock(true); // fair lock
    private final Condition notEmpty = lock.newCondition();

    /** Add a task to the buffer */
    public void put(Task task) throws InterruptedException {
        if (task == null) return; // safety check
        lock.lock();
        try {
            items.addLast(task);
            notEmpty.signal(); // wake one waiting worker
        } finally {
            lock.unlock();
        }
    }

    /** Take a task from the buffer (blocks if empty) */
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

    /** Clear all tasks in the buffer */
    public void clear() {
        lock.lock();
        try {
            items.clear();
        } finally {
            lock.unlock();
        }
    }
}