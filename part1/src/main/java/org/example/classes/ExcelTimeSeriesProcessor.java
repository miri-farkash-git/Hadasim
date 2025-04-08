package org.example.classes;

import org.example.subClasses.TimeEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.*;


public class ExcelTimeSeriesProcessor {

    public static void writeHourlyAveragesToCsv(Map<LocalDate, List<TimeEntry>> data, String outputPath) throws IOException {
        System.out.println("Writing hourly averages to CSV...");
        List<String> lines = new ArrayList<>();
        lines.add("date,hour,average");
        for (LocalDate date : data.keySet()) {
            Map<Integer, List<Double>> hourToValues = new HashMap<>();
            for (TimeEntry entry : data.get(date)) {
                int hour = entry.timestamp.getHour();
                double value = entry.value;
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    System.out.printf("⚠ Skipping invalid value at %s (%.3f)%n", entry.timestamp, value);
                    continue;
                }
                hourToValues.computeIfAbsent(hour, h -> new ArrayList<>()).add(value);
            }
            List<Integer> sortedHours = new ArrayList<>(hourToValues.keySet());
            Collections.sort(sortedHours);
            for (int hour : sortedHours) {
                List<Double> values = hourToValues.get(hour);
                if (values == null || values.isEmpty()) {
                    continue;
                }
                double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

                if (Double.isNaN(avg)) {
                    System.out.printf("🚨 NaN detected for %s hour %d — values: %s%n", date, hour, values);
                    continue;
                }

                lines.add(String.format("%s,%d,%.3f", date, hour, avg));
            }
        }

        File file = new File(outputPath);
        file.getParentFile().mkdirs();

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.println(line);
            }
        }
        System.out.println("✅ Final CSV created at: " + outputPath);
    }
}