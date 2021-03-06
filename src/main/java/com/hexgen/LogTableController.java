package com.hexgen;

/**
 * Created by anishjoseph on 25/04/18.
 */

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
    private TableColumn<LogRecord, Integer> rowNum;
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
    @FXML
    private TableColumn<LogRecord, String> event;
    private boolean tail = false;


    private ObservableList<LogRecord> masterData = null;
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yy HH:mm:ss.SSS");
    private FilteredList<LogRecord> filteredData = null;
    private Map<String, Filter> filters = new HashMap<>();
    private FilterController filterController;

    public LogTableController(ObservableList<LogRecord> masterData, boolean tail) {
        this.masterData = masterData;
        this.tail = tail;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     *
     * Initializes the table columns and sets up sorting and filtering.
     */
    @FXML
    private void initialize() {
        MenuGenerator menuGeneratorForThread =  new MenuGenerator(){
            @Override
            public ContextMenu getMenu(LogRecord logRecord) {
                ContextMenu contextMenu = new ContextMenu();
                if(logRecord == null || logRecord.threadProperty() == null) return  null;

                MenuItem openItem = new MenuItem("Filter on ThreadID");
                contextMenu.getItems().add(openItem);
                openItem.setOnAction(t -> {
                    filterController.addThreadFilter(logRecord.getThread());
                });

                if(logRecord.jobIdProperty()  != null) {
                    MenuItem combination = new MenuItem("Filter on ThreadID & JobID");
                    contextMenu.getItems().add(combination);
                    combination.setOnAction(t -> {
                        if(thread != null && jobId != null) {
                            filterController.addThreadFilter(logRecord.getThread());
                            filterController.addJobIdFilter(logRecord.getJobId().toString());
                        }
                    });
                }
                return contextMenu;
            }
        };
        MenuGenerator menuGeneratorForXaction = new MenuGenerator(){
            @Override
            public ContextMenu getMenu(LogRecord logRecord) {
                ContextMenu contextMenu = new ContextMenu();
                if(logRecord == null || logRecord.xActionIdProperty() == null) return  null;

                MenuItem openItem = new MenuItem("Filter on XID and JobId");
                contextMenu.getItems().add(openItem);
                openItem.setOnAction(t -> {
                    filters.get("xActionId").setEnabled(true);
                    filters.get("xActionId").setSearchSpec(logRecord.getxActionId().toString());
                    filterController.addJobIdFilter(logRecord.getJobId().toString());
                });
                return contextMenu;
            }
        };
        MenuGenerator menuGeneratorForJobId = new MenuGenerator(){
            @Override
            public ContextMenu getMenu(LogRecord logRecord) {
                ContextMenu contextMenu = new ContextMenu();
                if(logRecord == null || logRecord.jobIdProperty() == null) return  null;

                MenuItem openItem = new MenuItem("Filter on JobID");
                contextMenu.getItems().add(openItem);
                openItem.setOnAction(t -> {
                    filterController.addJobIdFilter(logRecord.getJobId().toString());
                });

                return contextMenu;
            }
        };

        filters.put("incompleteJobs", new Filter(false, ""));
        filters.put("timeAfter", new Filter(false, ""));
        filters.put("timeBefore", new Filter(false, ""));
        filters.put("jobId", new Filter(false, ""));
        filters.put("xActionId", new Filter(false, ""));
        filters.put("level", new Filter(false, ""));
        filters.put("company", new Filter(false, ""));
        filters.put("className", new Filter(false, ""));
        filters.put("user", new Filter(false, ""));
        filters.put("threadId", new Filter(false, ""));
        filters.put("regex", new Filter(false, ""));
        filters.put("msg", new Filter(false, ""));
        filters.put("event", new Filter(false, ""));
        filters.put("exceptions", new Filter(false, ""));
        filters.put("globalSearch", new Filter(true, ""));


//        logTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            /* Listener for when a row is clicked - right now we have nothing to do */
//        });

        level.setCellValueFactory(cellData -> cellData.getValue().levelProperty());
        user.setCellValueFactory(cellData -> cellData.getValue().userProperty());
        company.setCellValueFactory(cellData -> cellData.getValue().companyProperty());
        rowNum.setCellValueFactory(cellData -> cellData.getValue().idProperty());
        xActionId.setCellValueFactory(cellData -> cellData.getValue().xActionIdProperty());
        xActionId.setCellFactory(new MenuCallback<>(menuGeneratorForXaction));


        line.setCellValueFactory(cellData -> cellData.getValue().lineProperty());
        event.setCellValueFactory(cellData -> cellData.getValue().eventProperty());
        time.setCellValueFactory(cellData -> cellData.getValue().timeProperty());
        jobId.setCellValueFactory(cellData -> cellData.getValue().jobIdProperty());
        jobId.setCellFactory(new MenuCallback<>(menuGeneratorForJobId));

        className.setCellValueFactory(cellData -> cellData.getValue().classNameProperty());

        thread.setCellValueFactory(cellData -> cellData.getValue().threadProperty());
        thread.setCellFactory(new MenuCallback<>(menuGeneratorForThread));

        msg.setCellValueFactory(cellData -> cellData.getValue().msgProperty());
        msg.setCellFactory(col -> {
            TableCell msgTableCell = new TextFieldTableCell<String, String>()
            {
                @Override
                public void updateItem(String item, boolean empty)
                {
                    super.updateItem(item, empty);
                    setText( item );
                    Tooltip t = new Tooltip(item);
                    t.setStyle("-fx-font-size: 14px; -fx-background-color:yellow; -fx-text-fill:black");
                    setTooltip(t);
                }
            };
            msgTableCell.addEventHandler(MouseEvent.MOUSE_CLICKED, event1 -> {
                StringBuffer sb = new StringBuffer();
                LogRecord logRecord = (LogRecord) ((TableRow) msgTableCell.getParent()).getItem();
                ((SelectionModel)logTable.getSelectionModel()).select(msgTableCell.getIndex());
                if (logRecord != null) {
                    TextArea textArea = new TextArea();
                    textArea.setEditable(false);
                    textArea.setWrapText(true);
                    sb.append("Time \t\t: " + logRecord.getTimeFormatted() + "\n");
                    sb.append("Thread ID \t: " + logRecord.getThread() + "\n");
                    sb.append("Job ID \t\t: " + logRecord.getJobIdAsString() + "\n");
                    sb.append("XID ID \t\t: " + logRecord.getXidAsString() + "\n");
                    sb.append("User \t\t: " + logRecord.getUser() + "\n");
                    sb.append("Level \t\t: " + logRecord.getLevel() + "\n");
                    sb.append("Event \t\t: " + logRecord.getEvent() + "\n");
                    sb.append("Class \t\t: " + logRecord.getClassName() + "\n");
                    sb.append("Line \t\t\t: " + logRecord.getLine() + "\n");
                    sb.append("Message \t\t: " + logRecord.getMsg() + "\n");
                    if(logRecord.getAddlInfo() != null) {
                        sb.append(logRecord.getAddlInfo());
                    }
                    textArea.setText(sb.toString());
                    textArea.setPrefWidth(1000);
                    textArea.setPrefHeight(600);
                    textArea.setFont(msgTableCell.getFont());
                    Popup popup = new Popup();
                    popup.centerOnScreen();
                    popup.setAutoHide(true);
                    popup.getContent().add(textArea);
                    popup.show(msgTableCell.getScene().getWindow());
                }
            });

            return msgTableCell;
        });

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
            this.filters.get("globalSearch").setSearchSpec(newValue);
            filter();
        });

        // 3. Wrap the FilteredList in a SortedList.
        SortedList<LogRecord> sortedData = new SortedList<>(filteredData);

        // 4. Bind the SortedList comparator to the TableView comparator.
        sortedData.comparatorProperty().bind(logTable.comparatorProperty());

        // 5. Add sorted (and filtered) data to the table.
        logTable.getSortOrder().add(rowNum);
        if(tail) {
            rowNum.setComparator(rowNum.getComparator().reversed());
        }


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
            }else if (logRecord.eventProperty() != null && logRecord.getEvent().toLowerCase().contains(lowerCaseFilter)) {
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
    private void filterForXid(String searchStr, boolean firstCheck) {
        filteredData.setPredicate(logRecord -> {

            if(firstCheck) {
                logRecord.setHidden(false);
            } else {
                if(logRecord.isHidden()) {
                    return  false;
                }
            }
            if (logRecord.xActionIdProperty() != null && logRecord.getxActionId().toString().equals(searchStr)) {
                logRecord.setHidden(false);
                return true;
            }
            logRecord.setHidden(true);
            return false; // Does not match.
        });

    }
    private void filterForLevel(ObservableList<String > chosenLevels, boolean firstCheck) {
        filteredData.setPredicate(logRecord -> {

            if(firstCheck) {
                logRecord.setHidden(false);
            } else {
                if(logRecord.isHidden()) {
                    return  false;
                }
            }
            if (logRecord.levelProperty() != null && chosenLevels.contains(logRecord.getLevel())) {
                logRecord.setHidden(false);
                return true;
            }
            logRecord.setHidden(true);
            return false; // Does not match.
        });

    }
    private void filterForCompany(String searchStr, boolean firstCheck) {
        filteredData.setPredicate(logRecord -> {

            String upperCaseFilter = searchStr.toUpperCase();
            if(firstCheck) {
                logRecord.setHidden(false);
            } else {
                if(logRecord.isHidden()) {
                    return  false;
                }
            }
            if (logRecord.companyProperty() != null && logRecord.getCompany().contains(upperCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            }
            logRecord.setHidden(true);
            return false; // Does not match.
        });

    }
    private void filterForClassName(String searchStr, boolean firstCheck) {
        filteredData.setPredicate(logRecord -> {

            String lowerCaseFilter = searchStr.toLowerCase();
            if(firstCheck) {
                logRecord.setHidden(false);
            } else {
                if(logRecord.isHidden()) {
                    return  false;
                }
            }
            if (logRecord.classNameProperty() != null && logRecord.getClassName().toLowerCase().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            }
            logRecord.setHidden(true);
            return false; // Does not match.
        });
    }
    private void filterForExceptions(boolean firstCheck) {
        filteredData.setPredicate(logRecord -> {

            if(firstCheck) {
                logRecord.setHidden(false);
            } else {
                if(logRecord.isHidden()) {
                    return  false;
                }
            }
            if (logRecord.isException()) {
                logRecord.setHidden(true);
                return true;
            }
            logRecord.setHidden(true);
            return false; // Does not match.
        });
    }

    private void filterForUser(String searchStr, boolean firstCheck) {
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
            }
            logRecord.setHidden(true);
            return false; // Does not match.
        });

    }
    private void filterForMsg(String searchStr, boolean firstCheck) {
        filteredData.setPredicate(logRecord -> {

            String lowerCaseFilter = searchStr.toLowerCase();
            if(firstCheck) {
                logRecord.setHidden(false);
            } else {
                if(logRecord.isHidden()) {
                    return  false;
                }
            }
            if (logRecord.msgProperty() != null && logRecord.getMsg().toLowerCase().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            }
            logRecord.setHidden(true);
            return false; // Does not match.
        });

    }
    private void filterForEvent(String searchStr, boolean firstCheck) {
        filteredData.setPredicate(logRecord -> {

            String lowerCaseFilter = searchStr.toLowerCase();
            if(firstCheck) {
                logRecord.setHidden(false);
            } else {
                if(logRecord.isHidden()) {
                    return  false;
                }
            }
            if (logRecord.eventProperty() != null && logRecord.getEvent().toLowerCase().contains(lowerCaseFilter)) {
                logRecord.setHidden(false);
                return true;
            }
            logRecord.setHidden(true);
            return false; // Does not match.
        });

    }
    private void filterForAfterTime(LocalDateTime searchTime, boolean firstCheck) {
        filteredData.setPredicate(logRecord -> {

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
    private void filterForBeforeTime(LocalDateTime searchTime, boolean firstCheck) {
        filteredData.setPredicate(logRecord -> {

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

    private void filterForIncompleteJobs(Map<Integer, Boolean> incompleteJobs, boolean firstCheck) {
        filteredData.setPredicate(logRecord -> {
            List<Integer> jobids = incompleteJobs.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toList());


            if(firstCheck) {
                logRecord.setHidden(false);
            } else {
                if(logRecord.isHidden()) {
                    return  false;
                }
            }
            if (logRecord.jobIdProperty() != null && jobids.contains(logRecord.getJobId())) {
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
            if(!filter.isEnabled() || filter.getSearchSpec() == null || (filter.getSearchSpec() instanceof  String && ((String)filter.getSearchSpec()).isEmpty())) continue;
            if(filterName.equals("threadId"))
                filterForThreadId((String) filter.getSearchSpec(), firstCheck);
            if(filterName.equals("jobId"))
                filterForJobId((String) filter.getSearchSpec(), firstCheck);
            if(filterName.equals("xActionId"))
                filterForXid((String) filter.getSearchSpec(), firstCheck);
            if(filterName.equals("level"))
                filterForLevel((ObservableList<String>) filter.getSearchSpec(), firstCheck);
            if(filterName.equals("company"))
                filterForCompany((String) filter.getSearchSpec(), firstCheck);
            if(filterName.equals("event"))
                filterForEvent((String) filter.getSearchSpec(), firstCheck);
            if(filterName.equals("user"))
                filterForUser((String) filter.getSearchSpec(), firstCheck);
            if(filterName.equals("msg"))
                filterForMsg((String) filter.getSearchSpec(), firstCheck);
            if(filterName.equals("className"))
                filterForClassName((String) filter.getSearchSpec(), firstCheck);
            if(filterName.equals("exceptions"))
                filterForExceptions(firstCheck);
            if(filterName.equals("incompleteJobs"))
                filterForIncompleteJobs((Map<Integer, Boolean>) filter.getSearchSpec(), firstCheck);




            /* Dont put anything below this line */
            if(filterName.equals("globalSearch"))
                filterForGlobalSearch((String) filter.getSearchSpec(), firstCheck);
            firstCheck = false;
        }
        if(filters.get("timeAfter").isEnabled()){
            filterForAfterTime((LocalDateTime) filters.get("timeAfter").getSearchSpec(), firstCheck);
            firstCheck = false;
        }
        if(filters.get("timeBefore").isEnabled()){
            filterForBeforeTime((LocalDateTime) filters.get("timeBefore").getSearchSpec(), firstCheck);
            firstCheck = false;
        }

        if (firstCheck) {
            filterForGlobalSearch("", true);
        }

    }

    public Map<String, Filter> getFilters() {
        return filters;
    }

    public void setFilterController(FilterController filterController) {
        this.filterController = filterController;
    }

    public void incompleteJobsChanged() {
        if(filters.get("incompleteJobs").isEnabled()){
            filter();
        }
    }

    private class MenuCallback<T> implements Callback<TableColumn<LogRecord, T>, TableCell<LogRecord, T>> {
        private  MenuGenerator menuGenerator;
        public MenuCallback(MenuGenerator menuGenerator){
            this.menuGenerator = menuGenerator;
        }
        @Override
        public TableCell<LogRecord, T> call(TableColumn<LogRecord, T> col) {
            final TableCell tableCell = new TextFieldTableCell();
            tableCell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                LogRecord logRecord = (LogRecord) ((TableRow) tableCell.getParent()).getItem();
                ContextMenu menu = menuGenerator.getMenu(logRecord);
                if (menu != null) menu.show(tableCell.getParent(), event.getScreenX(), event.getScreenY());
            });
            return tableCell;
        }
    }
}