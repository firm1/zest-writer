package com.zestedesavoir.zestwriter;

import com.zestedesavoir.zestwriter.model.ExtractFile;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.view.MdTextController;
import com.zestedesavoir.zestwriter.view.MenuController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class MainApp extends Application {

    private Scene scene;
    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<ExtractFile> extracts = FXCollections.observableArrayList();
    private ObservableMap<String, String> contents = FXCollections.observableMap(new HashMap<>());
    private ZdsHttp zdsutils;
    private MdTextController Index;


    public MainApp() {
        super();
        Properties prop = new Properties();
        InputStream input = MainApp.class.getClassLoader().getResourceAsStream("config.properties");

        try {
            prop.load(input);
            zdsutils = new ZdsHttp(prop);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Scene getScene() {
        return scene;
    }

    public MdTextController getIndex() {
        return Index;
    }

    public ObservableMap<String, String> getContents() {
        return contents;
    }

    public ZdsHttp getZdsutils() {
        return zdsutils;
    }

    public ObservableList<ExtractFile> getExtracts() {
        return extracts;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Zest Writer");
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        initRootLayout();
        showWriter();
    }

    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Root.fxml"));
            rootLayout = loader.load();

            MenuController controller = loader.getController();
            controller.setMainApp(this);

            scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showWriter() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/Index.fxml"));
            AnchorPane writerLayout = loader.load();

            rootLayout.setCenter(writerLayout);


            MdTextController controller = loader.getController();
            controller.setMainApp(this);
            Index = controller;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
