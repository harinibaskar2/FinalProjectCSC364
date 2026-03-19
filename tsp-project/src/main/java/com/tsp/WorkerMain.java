package com.tsp;

import com.tsp.core.Buffer;
import com.tsp.core.Worker;
import com.tsp.network.MQTTClientManager;
import com.tsp.network.Outsourcer;

public class WorkerMain {

    public static void main(String[] args) {
        // Each worker needs a unique ID
        String workerId = "Worker-" + System.currentTimeMillis();
        String broker = "tcp://broker.hivemq.com:1883";

        // Buffer for worker threads to consume tasks
        Buffer buffer = new Buffer();

        // Initialize MQTT manager for this worker
        MQTTClientManager workerMQTT = new MQTTClientManager(
                broker,
                workerId,
                null // no ResultListener, only Coordinator listens
        );

        System.out.println(workerId + " MQTT connected. Subscribing to tasks...");

   
        workerMQTT.subscribeTasks(task -> {
            if (task == null) {
                System.out.println(workerId + ": Received null task!");
                return;
            }
            System.out.println(workerId + " received task: " + task.getStartIndex() + " -> " + task.getEndIndex());

            // Place task into buffer for worker threads
            new Thread(new Outsourcer(task, buffer)).start();
        });

        // Start worker threads
        int numWorkers = 4;
        for (int i = 1; i <= numWorkers; i++) {
            Thread t = new Thread(new Worker(buffer, workerMQTT, i));
            t.start();
        }

        System.out.println(workerId + " started. Waiting for tasks...");
    }
}

