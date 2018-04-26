package com.hexgen;

/**
 * Created by anishjoseph on 25/04/18.
 */
import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


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

    private LogTableController logTableController;

    @FXML
    private void initialize()
    {
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        tidText.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                logTableController.filterForThreadId(tidText.getText());
            });
            pause.playFromStart();
        });
        jobIdText.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> {
                logTableController.filterForJobId(jobIdText.getText());
            });
            pause.playFromStart();
        });
    }

    public void tidChkBoxClicked(Event e){
        if(tidChkbox.isSelected()){
            tidText.setVisible(true);
            String tidTex = tidText.getText();
            if(tidTex != null && !tidTex.isEmpty()) {
                logTableController.filterForThreadId(tidTex);
            }
        } else {
            tidText.setVisible(false);
            logTableController.filterForThreadId(null);
        }
    }
    public void jobIdChkBoxClicked(Event e){
        if(jobIdChkbox.isSelected()){
            jobIdText.setVisible(true);
            String jobIdTex = jobIdText.getText();
            if(jobIdTex != null && !jobIdTex.isEmpty()) {
                logTableController.filterForJobId(jobIdTex);
            }
        } else {
            jobIdText.setVisible(false);
            logTableController.filterForJobId(null);
        }
    }
    public void setLogTableController(LogTableController logTableController){
        this.logTableController = logTableController;
    }
}