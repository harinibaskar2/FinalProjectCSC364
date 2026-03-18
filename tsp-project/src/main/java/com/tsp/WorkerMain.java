package com.tsp;

import com.tsp.model.Task;
import com.tsp.network.MQTTClientManager;
import com.tsp.core.Buffer;
import com.tsp.core.Worker;
import com.tsp.network.Outsourcer;


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

        Buffer buffer = new Buffer(); // receive tasks and place into buffer
        int numWorkers = 4;
        for (int i = 1; i <= numWorkers; i++) {
            Thread t = new Thread(new Worker(buffer, workerMQTT, i));
            t.start();
        }

        System.out.println(workerId + " started. Waiting for tasks...");

        // Subscribe to tasks topic
        workerMQTT.subscribeTasks(new MQTTClientManager.TaskListener() {
            @Override
            public void onTaskReceived(Task task) {
                Thread t = new Thread(new Outsourcer(task, buffer));
                t.start();
            }
        });
    }
}


