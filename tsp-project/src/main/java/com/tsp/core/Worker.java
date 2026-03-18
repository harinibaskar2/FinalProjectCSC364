package com.tsp.core;

import com.tsp.model.Result;
import com.tsp.model.Task;
import com.tsp.network.MQTTClientManager;
import java.util.List;

public class Worker implements Runnable {

    private final MQTTClientManager mqtt;
    private final Buffer buffer;
    private final int workerNum;

    public Worker(Buffer buffer, MQTTClientManager mqtt, int workerNum) {
        this.buffer = buffer;
        this.mqtt = mqtt;
        this.workerNum = workerNum;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Task task = buffer.take(); // mqtt puts tasks in the buffer workers grab them from there
                if (task == null) {
                    continue;
                }
                if (task.isStopTask()) {
                    System.out.println("Worker: " + workerNum + " is exiting");
                    return;
                }
                List<Integer> tour = NearestNeighbors.solve(task.getCities(), 0);
                double length = NearestNeighbors.length(task.getCities(), tour);

                // worker threads handle results instead of WorkerMain
                Result result = new Result(tour, length);
                mqtt.publishResult(result);

                System.out.println(workerNum + " finished task: "
                + task.getStartIndex() + "-" + task.getEndIndex()
                + " | length: " + length);

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
