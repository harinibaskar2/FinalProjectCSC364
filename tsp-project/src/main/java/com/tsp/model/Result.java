package com.tsp.model;

import java.io.Serializable;
import java.util.List;

public class Result implements Serializable {
    private static final long serialVersionUID = 1L;

    private int startIndex;     // added
    private int endIndex;       // added
    private List<Integer> tour;
    private double length;

    public Result(int startIndex, int endIndex, List<Integer> tour, double length) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.tour = tour;
        this.length = length;
    }

    // getters
    public int getStartIndex() { return startIndex; }
    public int getEndIndex() { return endIndex; }
    public List<Integer> getTour() { return tour; }
    public double getLength() { return length; }
}