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
    private static final Pattern P1ThreadPattern  = Pattern.compile("\\s*Camel\\s*\\(.*?\\)\\s*thread\\s*#(\\d+)\\s*-\\s*JmsConsumer\\[(.*?)\\]");
    private static final Pattern P0ThreadPattern  = Pattern.compile("\\s*http-[n|b]io-\\d+-.*?-(\\d*)");
    private ObservableList<LogRecord> masterData = null;
    private boolean shutdown = false;
    private String logFilename;
    private boolean tail;


    public LogFileReader(ObservableList<LogRecord> masterData, String logFilename, boolean tail) {
        this.masterData = masterData;
        this.logFilename = logFilename;
        this.tail = tail;
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
        StringBuffer addlInfo = new StringBuffer();
        LogRecord logRecord = null;
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
//                        Camel (hexContext) thread #0 - JmsConsumer[p1]
                        String threadName = matcher.group(7).trim();
                        Matcher p1matcher = P1ThreadPattern.matcher(threadName);
                        if(p1matcher.matches()){
                            threadName = p1matcher.group(2) + " - Thd" + p1matcher.group(1);
                        } else {
                            Matcher p0matcher = P0ThreadPattern.matcher(threadName);
                            if(p0matcher.matches()){
                                threadName = "P0 - Thd" + p0matcher.group(1);
                            } else {
                                if(threadName.startsWith("wro4j-")) threadName = "wro";
                            }
                        }
                        if(addlInfo.length() > 0 && logRecord != null){
                            logRecord.setAddlInfo(addlInfo.toString());
                            addlInfo = new StringBuffer();
                        }
                        logRecord = new LogRecord(recordNumber, matcher.group(1).trim(),matcher.group(2).trim(),jobId,xActionId,matcher.group(5).trim(),localDateTime,threadName,matcher.group(8).trim(),matcher.group(9).trim(),lineNumber,matcher.group(11).trim());
                        masterData.add(logRecord);
                    } else {
                        addlInfo.append(line);
                        addlInfo.append("\n");
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

    public void process() {
        this.start();
    }
}
