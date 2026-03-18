package com.tsp.model;

import java.util.List;

public class Task {
    private int startIndex;
    private int endIndex;
    private List<City> cities;

    // Constructor
    public Task(int startIndex, int endIndex, List<City> cities) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.cities = cities;
    }

    // Getters / setters
    public int getStartIndex() { return startIndex; }
    public int getEndIndex() { return endIndex; }
    public List<City> getCities() { return cities; }
}


