package com.hexgen;

/**
 * Created by anishjoseph on 25/04/18.
 */

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


/**
 * View-Controller for the person table.
 *
 * @author Jayashree R Gowda (kappa)
 */
public class LogTableController extends Thread{

    @FXML
    private TextField filterField;
    @FXML
    private TableView<LogRecord> logTable;
    @FXML
    private TableColumn<LogRecord, String> level;
    @FXML
    private TableColumn<LogRecord, String> user;
    @FXML
    private TableColumn<LogRecord, String> company;
    @FXML
    private TableColumn<LogRecord, String> msg;
    @FXML
    private TableColumn<LogRecord, String> thread;
    @FXML
    private TableColumn<LogRecord, Integer> id;
    @FXML
    private TableColumn<LogRecord, Integer> xActionId;
    @FXML
    private TableColumn<LogRecord, Integer> jobId;
    @FXML
    private TableColumn<LogRecord, Integer> line;
    @FXML
    private TableColumn<LogRecord, String> className;
    @FXML
    private TableColumn<LogRecord, LocalDateTime> time;

    private static boolean isRunning = true;
    private ObservableList<LogRecord> masterData = null;
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm:ss.SSS");
    private FilteredList<LogRecord> filteredData = null;
    private Map<String, Filter> filters = new HashMap<>();
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     *
     * Initializes the table columns and sets up sorting and filtering.
     */
    @FXML
    private void initialize() {
        filters.put("timeAfter", new Filter(false, ""));
        filters.put("timeBefore", new Filter(false, ""));
        filters.put("jobId", new Filter(false, ""));
        filters.put("xActionId", new Filter(false, ""));
        filters.put("level", new Filter(false, ""));
        filters.put("user", new Filter(false, ""));
        filters.put("threadId", new Filter(false, ""));
        filters.put("regex", new Filter(false, ""));
        filters.put("msg", new Filter(false, ""));
        filters.put("globalSearch", new Filter(true, ""));

        masterData = LogFileReader.getMasterData();
        // 0. Initialize the columns.
        level.setCellValueFactory(cellData -> cellData.getValue().levelProperty());
        user.setCellValueFactory(cellData -> cellData.getValue().userProperty());
        company.setCellValueFactory(cellData -> cellData.getValue().companyProperty());
        msg.setCellValueFactory(cellData -> cellData.getValue().msgProperty());
        className.setCellValueFactory(cellData -> cellData.getValue().classNameProperty());
        thread.setCellValueFactory(cellData -> cellData.getValue().threadProperty());
        id.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        xActionId.setCellValueFactory(cellData -> cellData.getValue().xActionIdProperty());
        jobId.setCellValueFactory(cellData -> cellData.getValue().jobIdProperty());
        line.setCellValueFactory(cellData -> cellData.getValue().lineProperty());
        time.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        time.setCellFactory(col -> new TableCell<LogRecord, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(String.format(item.format(formatter)));
            }
        });

        // 1. Wrap the ObservableList in a FilteredList (initially display all data).
        filteredData = new FilteredList<>(masterData, p -> true);

        // 2. Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.filters.get("globalSearch").setSearchStr(newValue);
            filter();
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<LogRecord> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(logTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        logTable.setItems(sortedData);
    }
    private void filterForGlobalSearch(String searchStr, boolean firstCheck){
        filteredData.setPredicate(logRecord -> {
            String lowerCaseFilter = searchStr.toLowerCase();

            if(firstCheck) {
                logRecord.setHidden(false);
            } else {
                if(logRecord.isHidden()) {
                    return  false;
                }
            }
            if (logRecord.userProperty() != null && logRecord.getUser().toLowerCase().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            } else if (logRecord.companyProperty() != null && logRecord.getCompany().toLowerCase().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            } else if (logRecord.levelProperty() != null && logRecord.getLevel().toLowerCase().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            } else if (logRecord.classNameProperty() != null && logRecord.getClassName().toLowerCase().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            } else if (logRecord.msgProperty() != null && logRecord.getMsg().toLowerCase().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            } else if (logRecord.idProperty() != null && logRecord.getId().toString().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            } else if (logRecord.jobIdProperty() != null && logRecord.getJobId().toString().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            } else if (logRecord.xActionIdProperty() != null && logRecord.getxActionId().toString().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            } else if (logRecord.lineProperty() != null && logRecord.getLine().toString().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            } else if (logRecord.getTimeFormatted().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            }

            logRecord.setHidden(true);
            return false; // Does not match.
        });
    }

    private void filterForThreadId(String searchStr, boolean firstCheck){
        filteredData.setPredicate(logRecord -> {
            String lowerCaseFilter = searchStr.toLowerCase();

            if(firstCheck) {
                logRecord.setHidden(false);
            } else {
                if(logRecord.isHidden()) {
                    return  false;
                }
            }
            if (logRecord.threadProperty() != null && logRecord.getThread().toLowerCase().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            }
            logRecord.setHidden(true);
            return false; // Does not match.
        });
    }

    private void filterForJobId(String searchStr, boolean firstCheck) {
        filteredData.setPredicate(logRecord -> {

            String lowerCaseFilter = searchStr.toLowerCase();
            if(firstCheck) {
                logRecord.setHidden(false);
            } else {
                if(logRecord.isHidden()) {
                    return  false;
                }
            }
            if (logRecord.jobIdProperty() != null && logRecord.getJobId().toString().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            }
            logRecord.setHidden(true);
            return false; // Does not match.
        });

    }
    private void filterForAfterTime(String searchStr, boolean firstCheck) {
        filteredData.setPredicate(logRecord -> {

            LocalDateTime searchTime = LocalDateTime.parse(searchStr);
            if(firstCheck) {
                logRecord.setHidden(false);
            } else {
                if(logRecord.isHidden()) {
                    return  false;
                }
            }
            if (logRecord.jobIdProperty() != null && logRecord.getTime().isAfter(searchTime)) {
                logRecord.setHidden(false);
                return true;
            }
            logRecord.setHidden(true);
            return false; // Does not match.
        });
    }
    private void filterForBeforeTime(String searchStr, boolean firstCheck) {
        filteredData.setPredicate(logRecord -> {

            LocalDateTime searchTime = LocalDateTime.parse(searchStr);
            if(firstCheck) {
                logRecord.setHidden(false);
            } else {
                if(logRecord.isHidden()) {
                    return  false;
                }
            }
            if (logRecord.jobIdProperty() != null && logRecord.getTime().isBefore(searchTime)) {
                logRecord.setHidden(false);
                return true;
            }
            logRecord.setHidden(true);
            return false; // Does not match.
        });
    }

    public void filter(){
        boolean firstCheck = true;
        for (String filterName : filters.keySet()) {
            Filter filter = filters.get(filterName);
            if(filterName.equals("timeAfter") || filterName.equals("timeBefore")) continue;
            if(!filter.isEnabled() || filter.getSearchStr() == null || filter.getSearchStr().isEmpty()) continue;
            if(filterName.equals("threadId"))
                filterForThreadId(filter.getSearchStr(), firstCheck);
            if(filterName.equals("jobId"))
                filterForJobId(filter.getSearchStr(), firstCheck);




            /* Dont put anuthing below this line */
            if(filterName.equals("globalSearch"))
                filterForGlobalSearch(filter.getSearchStr(), firstCheck);
            firstCheck = false;
        }
        if(filters.get("timeAfter").isEnabled()){
            filterForAfterTime(filters.get("timeAfter").getSearchStr(), firstCheck);
            firstCheck = false;
        }
        if(filters.get("timeBefore").isEnabled()){
            filterForBeforeTime(filters.get("timeBefore").getSearchStr(), firstCheck);
            firstCheck = false;
        }

    }

    public Map<String, Filter> getFilters() {
        return filters;
    }
}