package com.tsp.core;

import com.tsp.model.Result;
import com.tsp.model.Task;
import com.tsp.network.MQTTClientManager;

public class Worker {

    private MQTTClientManager mqtt;
    private String workerId;

    public Worker(String workerId) {
        this.workerId = workerId;
        String broker = "tcp://test.mosquitto.org:1883";
        mqtt = new MQTTClientManager(broker, workerId, null);

        // Subscribe to tasks from Coordinator
        mqtt.subscribeTasks(this::executeTask);
    }

    public void processTask(Task task) {
        if (task == null || task.getCities() == null || task.getCities().isEmpty()) {
            return;
        }

        java.util.List<Integer> tour = NearestNeighbors.solve(task.getCities(), 0);
        double length = NearestNeighbors.length(task.getCities(), tour);

        Result result = new Result(tour, length);
        mqtt.publishResult(result);

        System.out.println(workerId + " finished task: "
                + task.getStartIndex() + "-" + task.getEndIndex()
                + " | length: " + length);
    }
}
