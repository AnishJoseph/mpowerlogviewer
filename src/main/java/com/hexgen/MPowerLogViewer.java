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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
                logFileReaderEntry.getValue().shutdown();
            }
        });
        if(!getParameters().getRaw().isEmpty()){
            open(getParameters().getRaw().get(0));
        }

    }

    private void open(String logFilename){
        try {
            ObservableList<LogRecord> masterData = FXCollections.observableArrayList();

            LogFileReader logFileReader = new LogFileReader(masterData, logFilename);
//            logFileReader.readFile(logFilename);

            FXMLLoader loader = new FXMLLoader(MPowerLogViewer.class.getResource("/fxml/LogTable.fxml"));
            LogTableController logTableController = new LogTableController(masterData);
            loader.setController(logTableController);
            AnchorPane logRecordsView = loader.load();

            Tab tab = new Tab();
            tab.setText(logFilename);
            tab.setOnCloseRequest(event -> {
                logFileReader.shutdown();
                openReaders.remove(tab.getId());
            } );

            BorderPane borderPane = new BorderPane();
            borderPane.setLeft(createFilterPane(logTableController));
            borderPane.setCenter(logRecordsView);
            tab.setContent(borderPane);
            tabPane.getTabs().add(tab);
            tabPane.getSelectionModel().select(tab);
            openReaders.put(tab.getId(), logFileReader);

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
        Preferences prefs = Preferences.userRoot().node("mPowerLogfileViewer");


        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open");
        fileMenu.getItems().add(openItem);
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().add(exitItem);

        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        helpMenu.getItems().add(aboutItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);
        menuBar.getMenus().add(helpMenu);


        openItem.setOnAction(e -> {
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
                open(selectedFile.getAbsolutePath());
                prefs.put("prevDirectory", selectedFile.getParent());
            }
        });
        aboutItem.setOnAction(e -> {
            System.out.println("Help->About Selected");
        });
        exitItem.setOnAction(e -> Platform.exit());

        return menuBar;
    }
    private Node createFilterPane(LogTableController logTableController){
        VBox filterVBox = null;
        try {
            FXMLLoader loader = new FXMLLoader(MPowerLogViewer.class.getResource("/fxml/filters.fxml"));
            filterVBox = loader.load();
            FilterController filterController = loader.getController();
            filterController.setLogTableController(logTableController);
            filterController.setFilters(logTableController.getFilters());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(4);
        }
        return filterVBox;
    }
}