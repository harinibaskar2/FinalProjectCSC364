package com.tsp;

import com.tsp.model.Task;
import com.tsp.model.Result;
import com.tsp.network.MQTTClientManager;
import com.tsp.core.NearestNeighbors;

import java.util.List;

public class WorkerMain {

    public static void main(String[] args) {
        // Each worker needs a unique ID
        String workerId = "Worker-" + System.currentTimeMillis();
        String broker = "tcp://test.mosquitto.org:1883";

        // Initialize MQTT manager for this worker
        MQTTClientManager workerMQTT = new MQTTClientManager(
                broker,
                workerId,
                null // no ResultListener, only Coordinator listens
        );

        System.out.println(workerId + " started. Waiting for tasks...");

        // Subscribe to tasks topic
        workerMQTT.subscribeTasks(new MQTTClientManager.TaskListener() {
            @Override
            public void onTaskReceived(Task task) {
                try {
                    // Run nearest neighbor on the received chunk
                    List<Integer> tour = NearestNeighbors.solve(task.getCities(), 0);
                    double length = NearestNeighbors.length(task.getCities(), tour);

                    // Wrap results in Result object
                    Result result = new Result(tour, length);

                    // Publish result back to Coordinator
                    workerMQTT.publishResult(result);

                    System.out.println(workerId + " completed task: " +
                            task.getStartIndex() + " -> " + task.getEndIndex());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}


