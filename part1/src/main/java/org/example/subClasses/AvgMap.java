package org.example.subClasses;

public class AvgMap {

    private double sum;
    private int count;

    public AvgMap(double initialValue) {
        this.sum = initialValue;
        this.count = 1;
    }

    public AvgMap set(double value) {
        this.sum += value;
        this.count++;
        return this;
    }

    public double getAvg() {
        return count == 0 ? 0 : sum / count; // Return 0 if no values are present
    }
}