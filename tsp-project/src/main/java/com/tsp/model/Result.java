package com.tsp.model;

import java.io.Serializable;
import java.util.List;

public class Result implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Integer> tour;
    private double length;

    public Result(List<Integer> tour, double length) {
        this.tour = tour;
        this.length = length;
    }

    public List<Integer> getTour() { return tour; }
    public double getLength() { return length; }
}


