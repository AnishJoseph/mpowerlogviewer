package com.hexgen;

/**
 * Created by anishjoseph on 25/04/18.
 */
import javafx.animation.PauseTransition;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
    private CheckBox timeChkbox;
    @FXML
    private CheckBox timeBeforeChkbox;
    @FXML
    private CheckBox timeAfterChkbox;

    @FXML
    private DatePicker timeAfterText;
    @FXML
    private DatePicker timeBeforeText;

    private LogTableController logTableController;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private Map<String, Filter> filters;

    @FXML
    private void initialize()
    {

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        tidText.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                filters.get("threadId").setSearchStr(tidText.getText());
                logTableController.filter();
            });
            pause.playFromStart();
        });
        jobIdText.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                filters.get("jobId").setSearchStr(jobIdText.getText());
                logTableController.filter();
            });
            pause.playFromStart();
        });
        timeAfterText.valueProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                if(newValue == null){
                    filters.get("timeAfter").setEnabled(false);
                    return;
                }
                LocalDateTime dt = newValue.atStartOfDay();
                filters.get("timeAfter").setSearchStr(dt.toString());
                filters.get("timeAfter").setEnabled(true);
                logTableController.filter();
            });
            pause.playFromStart();
        });
        timeBeforeText.valueProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                if(newValue == null){
                    filters.get("timeBefore").setEnabled(false);
                    return;
                }
                LocalDateTime dt = newValue.plusDays(1).atStartOfDay().minusSeconds(1);
                filters.get("timeBefore").setSearchStr(dt.toString());
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
            if(tidTex != null && !tidTex.isEmpty()) {
                logTableController.filter();
            }
        } else {
            tidText.setVisible(false);
            logTableController.filter();
        }
    }
    public void jobIdChkBoxClicked(Event e){
        jobIdText.setVisible(jobIdChkbox.isSelected());
        if(jobIdChkbox.isSelected()){
            filters.get("jobId").setEnabled(true);
            String jobIdTex = jobIdText.getText();
            if(jobIdTex != null && !jobIdTex.isEmpty()) {
                logTableController.filter();
            }
        } else {
            jobIdText.setVisible(false);
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