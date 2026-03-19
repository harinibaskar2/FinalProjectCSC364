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
                Task task = buffer.take();

                if (task.isStopTask()) {
                    System.out.println("Worker: " + workerNum + " is exiting");
                    return;
                }

                List<Integer> tour = NearestNeighbors.solve(task.getCities(), 0);
                double length = NearestNeighbors.length(task.getCities(), tour);

                Result result = new Result(task.getStartIndex(), task.getEndIndex(), tour, length);

                mqtt.publishResult(result);

                System.out.println("[Worker " + workerNum + "] finished task: "
                        + task.getStartIndex() + "-" + task.getEndIndex()
                        + " | length: " + length);

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
