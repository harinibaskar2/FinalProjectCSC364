package com.tsp.network;

import com.tsp.core.Buffer;
import com.tsp.model.Task;

public class Outsourcer implements Runnable {

    private final Task task;
    private final Buffer buffer;

    public Outsourcer(Task task, Buffer buffer) {
        this.task = task;
        this.buffer = buffer;
    }

    @Override
    public void run() {
        if (task == null) {
            System.out.println("Outsourcer: task is null.");
            return;
        }

        if (buffer == null) {
            System.out.println("Outsourcer: buffer is null.");
            return;
        }

        try {
            buffer.put(task); // place task in buffer for worker threads to get

            System.out.println("Queued task: " +
                task.getStartIndex() + " -> " + task.getEndIndex());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

    }
}