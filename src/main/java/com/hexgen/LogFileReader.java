package com.hexgen;

import javafx.collections.FXCollections;
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
    private static ObservableList<LogRecord> masterData = FXCollections.observableArrayList();
    private static boolean running = true;
    private static final Pattern pattern = Pattern.compile("(.*?)-(.*?)-(\\d*?)-(\\d*?):~:(.*?):~:(.*?):~:(.*?):~:(.*?):~:(.*?):~:(.*)");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm:ss.SSS");

    public static void setRunning(boolean running) {
        LogFileReader.running = running;
    }

    public static ObservableList<LogRecord> getMasterData() {
        return masterData;
    }

    private Long convertToLong(String value){
        value = value.trim();
        if(value.isEmpty())return null;
        return Long.parseLong(value);
    }

    public void readFile(String logFilename) {
        masterData.clear();
        String line = null;
        FileInputStream fs = null;
        BufferedReader br = null;
        try {
            fs = new FileInputStream(logFilename);
            br = new BufferedReader(new InputStreamReader(fs));
            Long recordNumber = 0L;
            while((line = br.readLine()) != null ) {
                recordNumber++;
                Matcher matcher = pattern.matcher(line);
                if(matcher.matches()) {
                    Long jobId = convertToLong(matcher.group(3));
                    Long xActionId = convertToLong(matcher.group(4));
                    Long lineNumber = convertToLong(matcher.group(9));
                    LocalDateTime localDateTime = LocalDateTime.parse(matcher.group(5), formatter);
                    masterData.add(new LogRecord(recordNumber, matcher.group(1),matcher.group(2),jobId,xActionId,localDateTime,matcher.group(6),matcher.group(7),matcher.group(8),lineNumber,matcher.group(10)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(3);

        }
    }
}
