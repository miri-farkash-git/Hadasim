package org.example.subClasses;

public class RunningStats {

    private double sum = 0;
    private int count = 0;

    public void add(double value) {
        sum += value;
        count++;
    }

    public double getAverage() {
        return count == 0 ? Double.NaN : sum / count;
    }

    public int getCount() {
        return count;
    }
}