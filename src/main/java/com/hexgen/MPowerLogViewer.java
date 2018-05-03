package com.hexgen;

/**
 * Created by anishjoseph on 25/04/18.
 */
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.application.Platform;
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
 * @author Marco Jakob
 */
public class MPowerLogViewer extends Application {
    private static String logFilename = null;

    private LogTableController logTableController;
    private LogFileReader logFileReader = null;
    private static Preferences prefs = Preferences.userRoot().node("mPowerLogfileViewer");


    @Override
    public void start(Stage primaryStage) {
        try {
            logFileReader = new LogFileReader();
            logFileReader.readFile(logFilename);
            FXMLLoader loader = new FXMLLoader(MPowerLogViewer.class.getResource("/fxml/LogTable.fxml"));

            AnchorPane logRecordsView = loader.load();
            logTableController = loader.getController();

            BorderPane border = new BorderPane();

            border.setTop(createMenus(primaryStage));
            border.setLeft(createFilterPane());
            border.setCenter(logRecordsView);

            Scene scene = new Scene(border, 1000, 800);
            primaryStage.setTitle("mPower Log Analyser");
            primaryStage.setScene(scene);

            primaryStage.setFullScreen(true);
            primaryStage.show();
            primaryStage.setOnCloseRequest(event -> {
                LogFileReader.setRunning(false);
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void usage(){
        System.out.println("Usage : MPowerLogViewer <mpower debug file>");
    }
    public static void main(String[] args) {
        if(args.length < 1){
            usage();
            System.exit(-1);
        }

        File file = new File(args[0]);
        if(!file.exists()){
            System.out.println("File " + args[0] + " does not exist!!. Exiting...");
            System.exit(-1);
        }
        logFilename = args[0];
        launch(args);
    }
    private MenuBar createMenus(Stage stage){
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());


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
                logFileReader.readFile(selectedFile.getAbsolutePath());
                prefs.put("prevDirectory", selectedFile.getParent());
            }
        });
        aboutItem.setOnAction(e -> {
            System.out.println("Help->About Selected");
        });
        exitItem.setOnAction(e -> Platform.exit());

        return menuBar;
    }
    private Node createFilterPane(){
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