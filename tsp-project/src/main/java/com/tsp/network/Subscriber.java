package com.tsp.network;

public class Subscriber {

    private final MQTTClientManager mqttClientManager;

    public Subscriber(MQTTClientManager mqttClientManager) {
        this.mqttClientManager = mqttClientManager;
    }

    public void subscribeToTasks(MQTTClientManager.TaskListener listener) {
        if (listener == null) {
            System.out.println("Subscriber: task listener is null.");
            return;
        }
        mqttClientManager.subscribeTasks(listener);
    }

    public void subscribeToResults(MQTTClientManager.ResultListener listener) {
        if (listener == null) {
            System.out.println("Subscriber: result listener is null.");
            return;
        }
        mqttClientManager.subscribeResults(listener);
    }
}