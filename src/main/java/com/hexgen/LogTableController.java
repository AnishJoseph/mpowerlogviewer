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

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     *
     * Initializes the table columns and sets up sorting and filtering.
     */
    @FXML
    private void initialize() {

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
            filteredData.setPredicate(logRecord -> {
                // If filter text is empty, display all records.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (logRecord.userProperty() != null && logRecord.getUser().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } else if (logRecord.companyProperty() != null && logRecord.getCompany().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } else if (logRecord.levelProperty() != null && logRecord.getLevel().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } else if (logRecord.classNameProperty() != null && logRecord.getClassName().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } else if (logRecord.msgProperty() != null && logRecord.getMsg().toLowerCase().contains(lowerCaseFilter)) {
                    return true; 
                } else if (logRecord.idProperty() != null && logRecord.getId().toString().contains(lowerCaseFilter)) {
                    return true; 
                } else if (logRecord.jobIdProperty() != null && logRecord.getJobId().toString().contains(lowerCaseFilter)) {
                    return true; 
                } else if (logRecord.xActionIdProperty() != null && logRecord.getxActionId().toString().contains(lowerCaseFilter)) {
                    return true; 
                } else if (logRecord.lineProperty() != null && logRecord.getLine().toString().contains(lowerCaseFilter)) {
                    return true;
                } else if (logRecord.getTimeFormatted().contains(lowerCaseFilter)) {
                    return true;
                }
                return false; // Does not match.
            });
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<LogRecord> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(logTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        logTable.setItems(sortedData);
    }

    public void filterForThreadId(String searchStr){
        filteredData.setPredicate(logRecord -> {
            if (searchStr == null || searchStr.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchStr.toLowerCase();

            if (logRecord.threadProperty() != null && logRecord.getThread().toLowerCase().contains(lowerCaseFilter)) {
                return true;
            }
            return false; // Does not match.
        });
    }

    public void filterForJobId(String searchStr) {
        filteredData.setPredicate(logRecord -> {
            if (searchStr == null || searchStr.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchStr.toLowerCase();

            if (logRecord.jobIdProperty() != null && logRecord.getJobId().toString().contains(lowerCaseFilter)) {
                return true;
            }
            return false; // Does not match.
        });

    }
}