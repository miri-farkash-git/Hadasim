package org.example.classes;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;



public class LogsProcessor {

    private final int MAX_ROWS_TO_PART = 1000; // Number of rows in each part
    private final String filePath;
    private final HashMap<String, Integer> errorsMap = new HashMap<>();

    public LogsProcessor(String filePath) {

        this.filePath = filePath;

    }

    public void sortToMap() {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();
            int partCount = (int) Math.ceil((double) totalRows / MAX_ROWS_TO_PART);
            // Iterate over rows and print content
            for (int part = 0; part < partCount; part++) {
                int startRow = part * MAX_ROWS_TO_PART;
                int endRow = Math.min(startRow + MAX_ROWS_TO_PART, totalRows);
                processPart(sheet, startRow, endRow);
            }
            // Print errorMap
            errorsMap.forEach((k, v) -> System.out.println(k + "\t" + v));
        } catch (IOException e) {
            System.out.println("Error reading the Excel file: " + e.getMessage());
        }
    }
    // Function to process a part of the rows
    private void processPart(Sheet sheet, int startRow, int endRow) {
        for (int rowIndex = startRow; rowIndex < endRow; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                String cell = row.getCell(0).toString();
                if (errorsMap.containsKey(cell)) {
                    errorsMap.put(cell, errorsMap.get(cell) + 1);
                } else {
                    errorsMap.put(cell, 1);
                }
            }
        }
    }

    public void getXTopErrors(int topX) {
        // Find the X keys with the highest values
        ArrayList<String> topKeys = (ArrayList<String>) errorsMap.entrySet().stream().sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Sort by values in descending order
                .limit(topX) // Limit the result to X values
                .map(Map.Entry::getKey) // Return only the keys
                .collect(Collectors.toList());
        // Print the keys with the highest values
        System.out.println("Top " + topX + " keys with the highest values:");
        topKeys.forEach((key) -> System.out.println(key + "\t" + errorsMap.get(key)));
    }
}