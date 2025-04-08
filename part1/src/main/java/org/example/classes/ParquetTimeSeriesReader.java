package org.example.classes;

import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;
import org.example.subClasses.TimeEntry;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParquetTimeSeriesReader {

    public static Map<LocalDate, List<TimeEntry>> readParquet(String filePath) throws Exception {
        Map<LocalDate, List<TimeEntry>> data = new HashMap<>();
        //try to read the file
        ParquetReader<GenericRecord> reader = AvroParquetReader.<GenericRecord>builder(new Path(filePath)).build();
        GenericRecord record;
        // default format if text
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        while ((record = reader.read()) != null) {
            Object tsRaw = record.get("timestamp");
            Object valRaw = record.get("mean_value");
            if (tsRaw == null || valRaw == null) continue;
            LocalDateTime timestamp;
            double value;
            // cast timestamp to LocalDateTime
            if (tsRaw instanceof CharSequence) {
                try {
                    timestamp = LocalDateTime.parse(tsRaw.toString(), formatter);
                } catch (Exception e) {
                    System.out.println("⚠️ Failed to parse timestamp string: " + tsRaw);
                    continue;
                }
            } else if (tsRaw instanceof Long) {
                try {
                    long nanos = (Long) tsRaw;
                    long millis = nanos / 1_000_000;
                    timestamp = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
                } catch (Exception e) {
                    System.out.println("⚠️ Failed to convert long timestamp: " + tsRaw);
                    continue;
                }
            } else {
                System.out.println("⚠️ Unsupported timestamp type: " + tsRaw.getClass());
                continue;
            }
            // cast value to double
            try {
                if (valRaw instanceof Number) {
                    value = ((Number) valRaw).doubleValue();
                } else {
                    value = Double.parseDouble(valRaw.toString());
                }
            } catch (Exception e) {
                System.out.println("⚠️ Failed to parse value: " + valRaw);
                continue;
            }
            // add to the map
            LocalDate date = timestamp.toLocalDate();
            data.computeIfAbsent(date, d -> new ArrayList<>())
                    .add(new TimeEntry(timestamp, value));
        }
        reader.close();
        System.out.println("✅ Parquet loaded: " + data.keySet().size() + " days of data.");
        return data;
    }
}