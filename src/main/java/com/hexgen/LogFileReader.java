package com.hexgen;

import javafx.collections.ObservableList;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by anishjoseph on 25/04/18.
 */
public class LogFileReader extends Thread {
    private static final Pattern pattern = Pattern.compile("(.*?):(.*?):(\\d*?):(\\d*?):(.*?):~:(.*?):~:(.*?):~:(.*?):~:(.*?):~:(.*?):~:(.*)");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm:ss.SSS");
    private ObservableList<LogRecord> masterData = null;
    private boolean shutdown = false;
    private String logFilename;

    public LogFileReader(ObservableList<LogRecord> masterData, String logFilename) {
        this.masterData = masterData;
        this.logFilename = logFilename;
        this.start();
    }

    private Long convertToLong(String value){
        value = value.trim();
        if(value.isEmpty())return null;
        return Long.parseLong(value);
    }

    public void run() {
        String line = null;
        FileInputStream fs = null;
        BufferedReader br = null;
        try {
            fs = new FileInputStream(logFilename);
            br = new BufferedReader(new InputStreamReader(fs));
            Long recordNumber = 0L;
            while(!shutdown) {
                if((line = br.readLine()) != null){
                    recordNumber++;
                    Matcher matcher = pattern.matcher(line);
                    if(matcher.matches()) {
                        Long jobId = convertToLong(matcher.group(3));
                        Long xActionId = convertToLong(matcher.group(4));
                        Long lineNumber = convertToLong(matcher.group(10));
                        LocalDateTime localDateTime = LocalDateTime.parse(matcher.group(6), formatter);
                        masterData.add(new LogRecord(recordNumber, matcher.group(1),matcher.group(2),jobId,xActionId,matcher.group(5),localDateTime,matcher.group(7),matcher.group(8),matcher.group(9),lineNumber,matcher.group(11)));
                    }

                } else {
                    sleep(100);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void shutdown() {
        this.shutdown = true;
    }
}
