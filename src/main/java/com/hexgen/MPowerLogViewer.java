package com.hexgen;

/**
 * Created by anishjoseph on 25/04/18.
 */
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Main class to start the application.
 *
 * @author Jayashree R Gowda (kappa)
 */
public class MPowerLogViewer extends Application {

    private TabPane tabPane;
    private Map<String, LogFileReader> openReaders = new HashMap<>();
    private Integer tabId = 1;

    @Override
    public void start(Stage primaryStage) {

        BorderPane border = new BorderPane();
        border.setTop(createMenus(primaryStage));
        this.tabPane = new TabPane();
        border.setCenter(tabPane);

        Scene scene = new Scene(border, 1000, 800);
        primaryStage.setTitle("mPower Log Analyser");
        primaryStage.setScene(scene);

        primaryStage.setFullScreen(true);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            for (Map.Entry<String, LogFileReader> logFileReaderEntry : openReaders.entrySet()) {
                System.out.println("Shutting down logFileReaderEntry = " + logFileReaderEntry.getKey());
                logFileReaderEntry.getValue().shutdown();
            }
        });
        if(!getParameters().getRaw().isEmpty()){
            open(getParameters().getRaw().get(0), false);
        }

    }

    private void open(String logFilename, boolean tail){
        try {
            ObservableList<LogRecord> masterData = FXCollections.observableArrayList();
            Map<Integer, Boolean> incompleteJobs = new HashMap<>();



            FXMLLoader logTableLoader = new FXMLLoader(MPowerLogViewer.class.getResource("/fxml/LogTable.fxml"));
            LogTableController logTableController = new LogTableController(masterData, tail);
            logTableLoader.setController(logTableController);
            AnchorPane logRecordsView = logTableLoader.load();
            LogFileReader logFileReader = new LogFileReader(masterData, logFilename, tail, incompleteJobs, logTableController);

            Tab tab = new Tab();
            tab.setText(logFilename);
            tab.setOnCloseRequest(event -> {
                logFileReader.shutdown();
                openReaders.remove(tab.getId());
            } );

            FilterController filterController = null;
            VBox filterVBox = null;
            try {
                FXMLLoader filterLoader = new FXMLLoader(MPowerLogViewer.class.getResource("/fxml/filters.fxml"));
                filterVBox = filterLoader.load();
                BackgroundImage myBI= new BackgroundImage(new Image("images/background.jpg",32,32,false,true),
                        BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT);
                filterVBox.setBackground(new Background(myBI));
                filterController = filterLoader.getController();
                filterController.setLogTableController(logTableController);
                filterController.setFilters(logTableController.getFilters());
                filterController.setIncompleteJobs(incompleteJobs);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(4);
            }
            logTableController.setFilterController(filterController);


            BorderPane borderPane = new BorderPane();
            borderPane.setLeft(filterVBox);
            filterVBox.setPadding(new Insets(10, 5, 0, 15));
            borderPane.setCenter(logRecordsView);
            tab.setContent(borderPane);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
            tab.setId(tabId.toString());
            tabId++;
            openReaders.put(tab.getId(), logFileReader);
            logFileReader.process();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static void usage(){
        System.out.println("Usage : MPowerLogViewer <mpower debug file>");
    }
    public static void main(String[] args) {
        launch(args);
    }
    private MenuBar createMenus(Stage stage){


        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open");
        fileMenu.getItems().add(openItem);
        MenuItem tailItem = new MenuItem("Tail");
        fileMenu.getItems().add(tailItem);
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().add(exitItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        helpMenu.getItems().add(aboutItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);
        menuBar.getMenus().add(helpMenu);


        tailItem.setOnAction(e -> open(stage, true));
        openItem.setOnAction(e -> open(stage, false));
        aboutItem.setOnAction(e -> {
            System.out.println("Help->About Selected");
        });
        exitItem.setOnAction(e -> Platform.exit());

        return menuBar;
    }
    private void open(Stage stage, boolean tail){
        Preferences prefs = Preferences.userRoot().node("mPowerLogfileViewer");
        String prevDirectory = prefs.get("prevDirectory", null);

        FileChooser fileChooser = new FileChooser();
        if(prevDirectory != null){
            File file = new File(prevDirectory);
            if(file.isDirectory()) {
                fileChooser.setInitialDirectory(file);
            }
        }
        fileChooser.setTitle("Choose the mPower debug file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("mPower Logs", "im_*.log")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);
        if(selectedFile != null){
            open(selectedFile.getAbsolutePath(), tail);
            prefs.put("prevDirectory", selectedFile.getParent());
        }

    }
}