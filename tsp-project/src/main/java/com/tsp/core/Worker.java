package com.tsp.core;

import com.tsp.model.Result;
import com.tsp.model.Task;
import com.tsp.network.MQTTClientManager;

public class Worker {

    private MQTTClientManager mqtt;

    public Worker(String workerId) {
        String broker = "tcp://test.mosquitto.org:1883";
        mqtt = new MQTTClientManager(broker, workerId, null);

        // Subscribe to tasks from Coordinator
        mqtt.subscribeTasks(this::processTask);
    }

    private void processTask(Task task) {
        if (task == null || task.getCities().isEmpty()) return;

        // Compute tour for this task
        java.util.List<Integer> tour = NearestNeighbors.solve(task.getCities(), 0);
        double length = NearestNeighbors.length(task.getCities(), tour);

        // Publish result back
        Result result = new Result(tour, length);
        mqtt.publishResult(result);

        System.out.println("Worker finished task: " + task.getStartIndex() + "-" + task.getEndIndex() + " | length: " + length);
    }
}

