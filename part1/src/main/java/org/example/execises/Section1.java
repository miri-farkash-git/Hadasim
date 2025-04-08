package org.example.execises;

import org.example.classes.LogsProcessor;

public class Section1 {

    public static void run(){

        String LOGS_FILE_PATH = "resources/logs.xlsx";
        int N = 3; // Number of most frequent errors to print
        LogsProcessor logsProcessor = new LogsProcessor(LOGS_FILE_PATH);
        System.out.println("sort to map");
        logsProcessor.sortToMap();

        System.out.println("get x top errors");
        logsProcessor.getXTopErrors(N);
    }
}