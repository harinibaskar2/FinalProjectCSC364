package com.tsp.network;

import com.tsp.core.Worker;
import com.tsp.model.Task;

public class Outsourcer implements Runnable {

    private final Task task;
    private final Worker worker;

    public Outsourcer(Task task, Worker worker) {
        this.task = task;
        this.worker = worker;
    }

    @Override
    public void run() {
        if (task == null) {
            System.out.println("Outsourcer: task is null.");
            return;
        }

        if (worker == null) {
            System.out.println("Outsourcer: worker is null.");
            return;
        }

        worker.processTask(task);
    }
}