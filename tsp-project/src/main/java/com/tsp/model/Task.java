package com.tsp.model;

import java.util.List;
import java.io.Serializable;

// add this to make task serializable bcs MQTT serializes task objects
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int startIndex;
    private final int endIndex;
    private final List<City> cities;
    private final boolean stopTask;


    // Constructor
    public Task(int startIndex, int endIndex, List<City> cities) {
        this(startIndex, endIndex, cities, false);
    }

    private Task(int startIndex, int endIndex, List<City> cities, boolean stopTask) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.cities = cities;
        this.stopTask = stopTask;
    }

    // add so workers can shutdown cleanly 
    public static Task stop() {
        return new Task(-1, -1, null, true);
    }

    // Getters / setters
    public int getStartIndex() { return startIndex; }
    public int getEndIndex() { return endIndex; }
    public List<City> getCities() { return cities; }
    public boolean isStopTask() { return stopTask; }
}


