package com.hexgen;

/**
 * Created by anishjoseph on 25/04/18.
 */
import javafx.beans.property.*;

import java.time.LocalDateTime;

/**
 * Simple model class for the person table.
 *
 * @author Marco Jakob
 */
public class LogRecord {
    private final SimpleObjectProperty<Integer> id;
    private final StringProperty user;
    private final StringProperty company;
    private final SimpleObjectProperty<Integer> jobId;
    private final SimpleObjectProperty<Integer> xActionId;
    private SimpleObjectProperty<LocalDateTime> time;
    private final StringProperty thread;
    private final StringProperty level;
    private final StringProperty className;
    private final SimpleObjectProperty<Integer> line;
    private final StringProperty msg;
    private final StringProperty event;
    private String timeFormatted = "";
    private boolean hidden = false;
    private String  addlInfo;
    private boolean exception = false;


    public LogRecord(Long id, String user, String company, Long jobId, Long xActionId, String event, LocalDateTime time, String thread, String level, String className, Long line, String msg) {
        this.id = id == null ? null : new SimpleObjectProperty(id.intValue());
        this.jobId = jobId == null ? null : new SimpleObjectProperty(jobId.intValue());
        this.xActionId = xActionId == null ? null : new SimpleObjectProperty(xActionId.intValue());
        this.line = line == null ? null : new SimpleObjectProperty(line.intValue());

        this.user = new SimpleStringProperty(user);
        this.company = new SimpleStringProperty(company);
        this.thread = new SimpleStringProperty(thread);
        this.level = new SimpleStringProperty(level);
        this.className = new SimpleStringProperty(className);
        this.msg = new SimpleStringProperty(msg);
        this.event = new SimpleStringProperty(event);

        this.time = new SimpleObjectProperty<>(time);
        if(time != null)
            this.timeFormatted = String.format(time.format(LogTableController.formatter)).toLowerCase();

    }

    public Integer getId() {
        return id.get();
    }

    public SimpleObjectProperty<Integer> idProperty() {
        return id;
    }

    public void setId(Integer id) {
        this.id.set(id);
    }

    public String getUser() {
        return user.get();
    }

    public StringProperty userProperty() {
        return user;
    }

    public void setUser(String user) {
        this.user.set(user);
    }

    public String getCompany() {
        return company.get();
    }

    public StringProperty companyProperty() {
        return company;
    }

    public void setCompany(String company) {
        this.company.set(company);
    }

    public String getXidAsString(){
        if(xActionId == null) return "";
        return xActionId.get().toString();
    }
    public String getJobIdAsString(){
        if(jobId == null) return "";
        return jobId.get().toString();
    }
    public Integer getJobId() {
        return jobId.get();
    }

    public SimpleObjectProperty<Integer> jobIdProperty() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId.set(jobId);
    }

    public Integer getxActionId() {
        return xActionId.get();
    }

    public SimpleObjectProperty<Integer> xActionIdProperty() {
        return xActionId;
    }

    public void setxActionId(Integer xActionId) {
        this.xActionId.set(xActionId);
    }

    public LocalDateTime getTime() {
        return time.get();
    }

    public SimpleObjectProperty<LocalDateTime> timeProperty() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time.set(time);
    }

    public String getThread() {
        return thread.get();
    }

    public StringProperty threadProperty() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread.set(thread);
    }

    public String getLevel() {
        return level.get();
    }

    public StringProperty levelProperty() {
        return level;
    }

    public void setLevel(String level) {
        this.level.set(level);
    }

    public String getClassName() {
        return className.get();
    }

    public StringProperty classNameProperty() {
        return className;
    }

    public void setClassName(String className) {
        this.className.set(className);
    }

    public Integer getLine() {
        return line.get();
    }

    public SimpleObjectProperty<Integer> lineProperty() {
        return line;
    }

    public void setLine(Integer line) {
        this.line.set(line);
    }

    public String getMsg() {
        return msg.get();
    }

    public StringProperty msgProperty() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg.set(msg);
    }

    public String getTimeFormatted() {
        return timeFormatted;
    }

    public void setTimeFormatted(String timeFormatted) {
        this.timeFormatted = timeFormatted;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getEvent() {
        return event.get();
    }

    public StringProperty eventProperty() {
        return event;
    }

    public void setEvent(String event) {
        this.event.set(event);
    }

    public String getAddlInfo() {
        return addlInfo;
    }

    public void setAddlInfo(String addlInfo) {
        this.addlInfo = addlInfo;
    }

    public boolean isException() {
        return exception;
    }

    public void setException(boolean exception) {
        this.exception = exception;
    }
}