package com.hexgen;

/**
 * Created by anishjoseph on 25/04/18.
 */
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.controlsfx.control.CheckComboBox;
import tornadofx.control.DateTimePicker;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


/**
 * View-Controller for the person table.
 *
 * @author Jayashree R Gowda (kappa)
 */
public class FilterController extends Thread{

    @FXML
    private TextField tidText;
    @FXML
    private CheckBox tidChkbox;
    @FXML
    private TextField jobIdText;
    @FXML
    private CheckBox jobIdChkbox;
    @FXML
    private CheckComboBox<String> levelText;

    @FXML
    private CheckBox levelChkbox;
    @FXML
    private TextField companyText;
    @FXML
    private CheckBox companyChkbox;
    @FXML
    private TextField eventText;
    @FXML
    private CheckBox eventChkbox;
    @FXML
    private CheckBox timeChkbox;
    @FXML
    private CheckBox timeBeforeChkbox;
    @FXML
    private CheckBox timeAfterChkbox;

    @FXML
    private DateTimePicker timeAfterText;
    @FXML
    private DateTimePicker timeBeforeText;

    private LogTableController logTableController;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private Map<String, Filter> filters;

    @FXML
    private void initialize()
    {
        final ObservableList<String> strings = FXCollections.observableArrayList();
        strings.add("ERROR");
        strings.add("WARN");
        strings.add("INFO");
        strings.add("DEBUG");
        strings.add("TRACE");
        levelText.getItems().addAll(strings);

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        tidText.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                filters.get("threadId").setSearchSpec(tidText.getText());
                logTableController.filter();
            });
            pause.playFromStart();
        });
        jobIdText.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                filters.get("jobId").setSearchSpec(jobIdText.getText());
                logTableController.filter();
            });
            pause.playFromStart();
        });
        levelText.getCheckModel().getCheckedItems().addListener((ListChangeListener<String>) c -> {
            pause.setOnFinished(event -> {
                filters.get("level").setSearchSpec(levelText.getCheckModel().getCheckedItems());
                logTableController.filter();
            });
            pause.playFromStart();
        });
        companyText.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                filters.get("company").setSearchSpec(companyText.getText());
                logTableController.filter();
            });
            pause.playFromStart();
        });
        eventText.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                filters.get("event").setSearchSpec(eventText.getText());
                logTableController.filter();
            });
            pause.playFromStart();
        });


        timeAfterText.dateTimeValueProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                if(newValue == null){
                    filters.get("timeAfter").setEnabled(false);
                    return;
                }
                filters.get("timeAfter").setSearchSpec(timeAfterText.getDateTimeValue());
                filters.get("timeAfter").setEnabled(true);
                logTableController.filter();
            });
            pause.playFromStart();
        });
        timeBeforeText.dateTimeValueProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                if (newValue == null) {
                    filters.get("timeBefore").setEnabled(false);
                    return;
                }
                filters.get("timeBefore").setSearchSpec(timeBeforeText.getDateTimeValue());
                filters.get("timeBefore").setEnabled(true);
                logTableController.filter();
            });
            pause.playFromStart();
        });
    }

    public void tidChkBoxClicked(Event e){
        filters.get("threadId").setEnabled(tidChkbox.isSelected());
        if(tidChkbox.isSelected()){
            tidText.setVisible(true);
            String tidTex = tidText.getText();
            if(!tidTex.isEmpty()) filters.get("threadId").setSearchSpec(tidTex);
            if(tidTex != null && !tidTex.isEmpty()) {
                logTableController.filter();
            }
        } else {
            tidText.setVisible(false);
            filters.get("threadId").setSearchSpec(null);
            logTableController.filter();
        }
    }
    public void jobIdChkBoxClicked(Event e){
        jobIdText.setVisible(jobIdChkbox.isSelected());
        if(jobIdChkbox.isSelected()){
            filters.get("jobId").setEnabled(true);
            String jobIdTex = jobIdText.getText();
            if(!jobIdTex.isEmpty()) filters.get("jobId").setSearchSpec(jobIdTex);
            if(jobIdTex != null && !jobIdTex.isEmpty()) {
                logTableController.filter();
            }
        } else {
            jobIdText.setVisible(false);
            filters.get("jobId").setSearchSpec(null);
            logTableController.filter();
        }
    }
    public void levelChkBoxClicked(Event e){
        levelText.setVisible(levelChkbox.isSelected());
        if(levelChkbox.isSelected()){
            filters.get("level").setEnabled(true);
            ObservableList<String> checkedItems = levelText.getCheckModel().getCheckedItems();
            if(!checkedItems.isEmpty()) filters.get("level").setSearchSpec(checkedItems);
            if(!checkedItems.isEmpty()) {
                logTableController.filter();
            }
        } else {
            levelText.setVisible(false);
            filters.get("level").setSearchSpec(null);
            logTableController.filter();
        }
    }
    public void companyChkBoxClicked(Event e){
        companyText.setVisible(companyChkbox.isSelected());
        if(companyChkbox.isSelected()){
            filters.get("company").setEnabled(true);
            String companyTex = companyText.getText();
            if(!companyTex.isEmpty()) filters.get("company").setSearchSpec(companyTex);
            if(companyTex != null && !companyTex.isEmpty()) {
                logTableController.filter();
            }
        } else {
            companyText.setVisible(false);
            filters.get("company").setSearchSpec(null);
            logTableController.filter();
        }
    }
    public void eventChkBoxClicked(Event e){
        eventText.setVisible(eventChkbox.isSelected());
        if(eventChkbox.isSelected()){
            filters.get("event").setEnabled(true);
            String eventTex = eventText.getText();
            if(!eventTex.isEmpty()) filters.get("event").setSearchSpec(eventTex);
            if(eventTex != null && !eventTex.isEmpty()) {
                logTableController.filter();
            }
        } else {
            eventText.setVisible(false);
            filters.get("event").setSearchSpec(null);
            logTableController.filter();
        }
    }
    public void timeChkBoxClicked(Event e){
        filters.get("timeAfter").setEnabled(timeAfterChkbox.isSelected());
        filters.get("timeBefore").setEnabled(timeBeforeChkbox.isSelected());
        if(timeChkbox.isSelected()){
            timeAfterChkbox.setVisible(true);
            timeBeforeChkbox.setVisible(true);
            timeAfterText.setVisible(true);
            timeBeforeText.setVisible(true);

            filters.get("timeAfter").setEnabled(timeAfterText.getValue() != null);
            filters.get("timeBefore").setEnabled(timeBeforeText.getValue() != null);

            if(timeAfterText.getValue() != null || timeAfterText != null){
                logTableController.filter();
            }
        } else {
            timeAfterChkbox.setVisible(false);
            timeBeforeChkbox.setVisible(false);
            timeAfterText.setVisible(false);
            timeBeforeText.setVisible(false);
            logTableController.filter();
        }
    }
    public void setLogTableController(LogTableController logTableController){
        this.logTableController = logTableController;
    }

    public void setFilters(Map<String, Filter> filters) {
        this.filters = filters;
    }
}