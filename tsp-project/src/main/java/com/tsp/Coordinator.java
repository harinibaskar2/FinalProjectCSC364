package com.tsp;

import java.util.ArrayList;
import java.util.List;

import com.tsp.model.City;
import com.tsp.model.Result;
import com.tsp.model.Task;
import com.tsp.network.MQTTClientManager;

public class Coordinator {

    private List<City> cities = new ArrayList<>();
    private int taskSize = 500;
    private MQTTClientManager mqtt;
    private List<Integer> bestTour = new ArrayList<>();
    private double bestLength = Double.POSITIVE_INFINITY;

    public Coordinator() {
        // connect to MQTT broker
        mqtt = new MQTTClientManager(
                "tcp://test.mosquitto.org:1883",
                "Coordinator-" + System.currentTimeMillis(),
                this::receiveResult // callback for results
        );
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public void distributeTasks() {
        int n = cities.size();
        for (int start = 0; start < n; start += taskSize) {
            int end = Math.min(start + taskSize, n);
            Task task = new Task(start, end, cities.subList(start, end));
            mqtt.publishTask(task);
        }
    }

    public synchronized void receiveResult(Result result) {
        if (result.getLength() < bestLength) {
            bestLength = result.getLength();
            bestTour = result.getTour();
            System.out.println("New best tour length: " + bestLength);
        }
    }

    public List<Integer> getBestTour() { return bestTour; }
    public double getBestLength() { return bestLength; }
}