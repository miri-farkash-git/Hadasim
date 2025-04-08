package org.example.subClasses;

import java.time.LocalDateTime;

public class TimeEntry {

    public LocalDateTime timestamp;
    public double value;

    public TimeEntry(LocalDateTime timestamp, double value) {
        this.timestamp = timestamp;
        this.value = value;
    }
}