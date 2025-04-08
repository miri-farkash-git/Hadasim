package org.example.execises;

import org.apache.poi.util.IOUtils;
import org.example.classes.ExcelTimeSeriesProcessor;
import org.example.classes.ParquetTimeSeriesReader;
import org.example.classes.TimeSeriesReader;
import org.example.subClasses.TimeEntry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;



public class Section2 {

    public static void run() {

        // part 1 - read and validate

        String INPUT_PATH = "resources/time_series.xlsx";
        System.out.println("time series reader...");
        TimeSeriesReader timeSeriesReader = new TimeSeriesReader(INPUT_PATH);

        //א - validation
        System.out.println("Validating data...");
        timeSeriesReader.validation();
        //ב - sort per hour

        System.out.println("Sorting data by hour...");
        timeSeriesReader.dividePerHours();

        // part 2 - write to csv
        String OUTPUT_PATH = "outputs/hourly_averages_final.csv";
        try {
            IOUtils.setByteArrayMaxOverride(200000000);
            System.out.println("write hour avarage to csv");
            Map<LocalDate, List<TimeEntry>> dailyData = timeSeriesReader.splitByDay(INPUT_PATH);
            timeSeriesReader.writeHourlyAveragesToCsv(dailyData, OUTPUT_PATH);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // part 3  - streaming
        System.out.println("Starting streaming simulation...\n");
        Random random = new Random();
        //simulation of streaming: sending 100 random values over 3 different days
        LocalDateTime baseTime = LocalDateTime.of(2025, 6, 25, 0, 0);
        for (int i = 0; i < 100; i++) {
            int hourOffset = random.nextInt(72); // up to 3 days
            double value = 48 + random.nextDouble() * 4; // value between 48 and 52
            LocalDateTime time = baseTime.plusHours(hourOffset);
            timeSeriesReader.process(time, value);
            try {
                Thread.sleep(50); // simulate a delay of 50 milliseconds between each value
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }



        System.out.println("\n✅ Finished processing stream.\n");

        timeSeriesReader.printAllAverages();



        // part 4 - parquet

        System.out.println("Reading from Parquet file...\n");

        try {
            String inputParquet = "resources/time_series.parquet";
            String outputCsv = "outputs/hourly_averages_from_parquet.csv";
            Map<LocalDate, List<TimeEntry>> data = ParquetTimeSeriesReader.readParquet(inputParquet);
            ExcelTimeSeriesProcessor.writeHourlyAveragesToCsv(data, outputCsv);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}