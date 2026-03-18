package com.tsp.network;

import com.tsp.model.Result;
import com.tsp.model.Task;

public class Publisher {

    private final MQTTClientManager mqttClientManager;

    public Publisher(MQTTClientManager mqttClientManager) {
        this.mqttClientManager = mqttClientManager;
    }

    public void publishTask(Task task) {
        if (task == null) {
            System.out.println("Publisher: task is null, nothing to publish.");
            return;
        }
        mqttClientManager.publishTask(task);
    }

    public void publishResult(Result result) {
        if (result == null) {
            System.out.println("Publisher: result is null, nothing to publish.");
            return;
        }
        mqttClientManager.publishResult(result);
    }
}