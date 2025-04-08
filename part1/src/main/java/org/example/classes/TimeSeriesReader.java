package org.example.classes;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.subClasses.AvgMap;
import org.example.subClasses.RunningStats;
import org.example.subClasses.TimeEntry;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TimeSeriesReader {

    private String filePath;
    private HashMap<String, String> timesMap = new HashMap<>();
    private HashMap<Number, AvgMap> startTimesMapAvg = new HashMap<>();

    public TimeSeriesReader(String filePath) {
        this.filePath = filePath;
    }

    private boolean isValidateDate(String date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;}
    }

    public void validation() {
        IOUtils.setByteArrayMaxOverride(200000000);
        DataFormatter dataFormatter = new DataFormatter(); // יצירת אובייקט DataFormatter
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);
            // Create a map to store times
            String dateFormat = "dd/MM/yyyy HH:mm";
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                Cell cellTime = row.getCell(0);
                Cell cellValue = row.getCell(1);
                if (cellTime != null && cellTime.getCellType() == CellType.STRING) {
                    String date = cellTime.getStringCellValue();
                    if (!isValidateDate(date, dateFormat)) {
                        System.out.println("error in row " + row.getRowNum() + ". date: " + date + ". is not valid.");
                    }
                }
                if (timesMap.containsKey(cellTime)) {
                    System.out.println("error in row " + row.getRowNum() + ". the datetime: " + dataFormatter.formatCellValue(cellTime) + " is already exist.");
                }
                // Check if the cell value is null
                if (cellValue == null) {
                    System.out.println("error in row " + row.getRowNum() + ". value is null.");
                }
                // Check if the cell value is not a positive number
                else if (cellValue.getCellType() != CellType.NUMERIC || cellValue.getNumericCellValue() < 0) {
                    System.out.println("error in row " + row.getRowNum() + ". value: " + cellValue + " is not valid number.");
                } else {
                    timesMap.put(dataFormatter.formatCellValue(cellTime), cellValue.toString());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the Excel file: " + e.getMessage());
        }
    }

    public void dividePerHours() {

        startTimesMapAvg.clear(); // Clear the map to ensure no stale data
        IOUtils.setByteArrayMaxOverride(200000000); // Set a limit for byte array size
        DataFormatter dataFormatter = new DataFormatter(); // Formatter for cell values
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis)) {
            // Iterate through rows in the first sheet
            for (Row row : workbook.getSheetAt(0)) {
                Cell cellTime = row.getCell(0); // Time column
                Cell cellValue = row.getCell(1); // Value column
                if (cellTime != null && cellValue != null && cellValue.getCellType() == CellType.NUMERIC) {
                    // Extract the hour key (e.g., "dd/MM/yyyy HH")
                    String dateTime = dataFormatter.formatCellValue(cellTime);
                    String hourKey = dateTime.substring(dateTime.indexOf(' ') + 1, dateTime.indexOf(':'));
                    // Get the numeric value and update the AvgMap
                    double value = cellValue.getNumericCellValue();
                    startTimesMapAvg.computeIfAbsent(Integer.parseInt(hourKey), k -> new AvgMap(value)).set(value);
                }
            }
            // Print the average values for each hour
            startTimesMapAvg.forEach((hour, avgMap) -> System.out.println(hour + "\t" + avgMap.getAvg()));
        } catch (IOException e) {
            System.err.println("Error reading the Excel file: " + e.getMessage());
        }
    }

    // Step 1: Read Excel and group by day
    public static Map<LocalDate, List<TimeEntry>> splitByDay(String inputPath) throws Exception {
        Map<LocalDate, List<TimeEntry>> data = new HashMap<>();

        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(inputPath))) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean isHeader = true;

            for (Row row : sheet) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }

                Cell tsCell = row.getCell(0);
                Cell valCell = row.getCell(1);

                if (tsCell == null || valCell == null) continue;
                if (tsCell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(tsCell)) continue;

                LocalDateTime timestamp = tsCell.getLocalDateTimeCellValue();
                double value;

                try {
                    if (valCell.getCellType() == CellType.NUMERIC) {
                        value = valCell.getNumericCellValue();
                    } else if (valCell.getCellType() == CellType.STRING) {
                        value = Double.parseDouble(valCell.getStringCellValue());
                    } else {
                        continue;
                    }
                } catch (NumberFormatException e) {
                    continue;
                }

                LocalDate date = timestamp.toLocalDate();
                data.computeIfAbsent(date, d -> new ArrayList<>())
                        .add(new TimeEntry(timestamp, value));
            }
        }

        System.out.println("✅ Loaded and grouped data by date: " + data.keySet().size() + " days.");
        return data;
    }
    // Step 2: Compute hourly averages and write to CSV
    public static void writeHourlyAveragesToCsv(Map<LocalDate, List<TimeEntry>> data, String outputPath) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add("date,hour,average");
        for (LocalDate date : data.keySet()) {
            Map<Integer, List<Double>> hourToValues = new HashMap<>();
            for (TimeEntry entry : data.get(date)) {
                int hour = entry.timestamp.getHour();
                double value = entry.value;
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    continue;
                }
                hourToValues.computeIfAbsent(hour, h -> new ArrayList<>()).add(value);
            }

            List<Integer> sortedHours = new ArrayList<>(hourToValues.keySet());
            Collections.sort(sortedHours);

            for (int hour : sortedHours) {
                List<Double> values = hourToValues.get(hour);
                if (values == null || values.isEmpty()) {
                    System.out.printf("⚠ No values at %s hour %d — skipping%n", date, hour);
                    continue;
                }

                double avg = values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

                if (Double.isNaN(avg)) {
                    System.out.printf("🚨 WARNING: NaN detected for %s hour %d — values: %s%n", date, hour, values);
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

    private final Map<LocalDate, Map<Integer, RunningStats>> stats = new HashMap<>();

    public void process(LocalDateTime timestamp, double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return;
        LocalDate date = timestamp.toLocalDate();
        int hour = timestamp.getHour();
        stats
                .computeIfAbsent(date, d -> new HashMap<>())
                .computeIfAbsent(hour, h -> new RunningStats())
                .add(value);

        RunningStats stat = stats.get(date).get(hour);
        System.out.printf("⏱ %s %02d:00 → avg: %.3f (n=%d)%n",
                date, hour, stat.getAverage(), stat.getCount());
    }

    public void printAllAverages() {
        System.out.println("📊 Final hourly averages:");
        stats.keySet().stream().sorted().forEach(date -> {
            Map<Integer, RunningStats> hours = stats.get(date);
            hours.keySet().stream().sorted().forEach(hour -> {
                RunningStats rs = hours.get(hour);
                System.out.printf("%s,%02d,%.3f%n", date, hour, rs.getAverage());
            });
        });
    }
}