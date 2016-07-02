package com.zestedesavoir.zestwriter;

import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.view.MdTextController;
import com.zestedesavoir.zestwriter.view.MenuController;
import com.zestedesavoir.zestwriter.view.com.CustomAlert;
import com.zestedesavoir.zestwriter.view.com.CustomFXMLLoader;
import com.zestedesavoir.zestwriter.view.com.FunctionTreeFactory;
import com.zestedesavoir.zestwriter.view.task.LoginService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MainApp extends Application {
    public static Configuration config;
    private static Stage primaryStage;
    private static ZdsHttp zdsutils;
    private Scene scene;
    private BorderPane rootLayout;
    private ObservableMap<Textual, Tab> extracts = FXCollections.observableMap(new HashMap<>());
    private ObservableList<Content> contents = FXCollections.observableArrayList();
    private MdTextController Index;
    private StringBuilder key = new StringBuilder();
    private Logger logger;
    private MenuController menuController;
    public static String[] args;
    public static File defaultHome;

    public static final int SIZE_WINDOW_MIN_WIDTH = 800;
    public static final int SIZE_WINDOW_MIN_HEIGHT = 500;

    public MainApp() {
        super();
        logger = LoggerFactory.getLogger(MenuController.class);

        if(args != null && args.length > 0) {
            config = new Configuration(args[0]);
        } else {
            File sample = new File(System.getProperty("user.home"));
            if(sample.canWrite()) {
                defaultHome = sample;
            } else {
                JFileChooser fr = new JFileChooser();
                FileSystemView fw = fr.getFileSystemView();
                defaultHome = fw.getDefaultDirectory();
            }
            logger.info("Répertoire Home par defaut : "+defaultHome);
            config = new Configuration(defaultHome.getAbsolutePath());
        }
        zdsutils = new ZdsHttp(config);

    }

    public static void main(String[] args) {
        MainApp.args  = args;
        launch(args);
    }


    public static Configuration getConfig() {
        return config;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static ZdsHttp getZdsutils() {
        return zdsutils;
    }

    public Scene getScene() {
        return scene;
    }

    public MdTextController getIndex() {
        return Index;
    }

    public ObservableList<Content> getContents() {
        return contents;
    }

    public ObservableMap<Textual, Tab> getExtracts() {
        return extracts;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Zest Writer");
        this.primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("assets/static/icons/logo.png")));
        this.primaryStage.setMinWidth(SIZE_WINDOW_MIN_WIDTH);
        this.primaryStage.setMinHeight(SIZE_WINDOW_MIN_HEIGHT);

        if(config.isDisplayWindowMaximize()){
            MainApp.primaryStage.setMaximized(true);
        }else{
            if(config.isDisplayWindowPersonnalDimension()){
                MainApp.primaryStage.setWidth(config.getDisplayWindowWidth());
                MainApp.primaryStage.setHeight(config.getDisplayWindowHeight());
            }else{
                MainApp.primaryStage.setWidth(Double.parseDouble(Configuration.ConfigData.DisplayWindowWidth.getDefaultValue()));
                MainApp.primaryStage.setHeight(Double.parseDouble(Configuration.ConfigData.DisplayWindowHeight.getDefaultValue()));
            }
            if(config.isDisplayWindowPersonnalPosition()){
                MainApp.primaryStage.setX(config.getDisplayWindowPositionX());
                MainApp.primaryStage.setY(config.getDisplayWindowPositionY());
            }
        }


        MainApp.primaryStage.setOnCloseRequest(t -> {
            quitApp();
            t.consume();
        });
        MainApp.primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            config.setDisplayWindowWidth(String.valueOf(newValue));
        });
        MainApp.primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            config.setDisplayWindowHeight(String.valueOf(newValue));
        });
        MainApp.primaryStage.xProperty().addListener((observable, oldValue, newValue) -> {
            config.setDisplayWindowPositionX(String.valueOf(newValue));
        });
        MainApp.primaryStage.yProperty().addListener((observable, oldValue, newValue) -> {
            config.setDisplayWindowPositionY(String.valueOf(newValue));
        });

        initRootLayout();
        showWriter();
        initConnection();
    }

    @FXML public void exitApplication(ActionEvent event) {
       quitApp();
    }

    public void quitApp() {
        if(primaryStage.isMaximized() && config.isDisplayWindowPersonnalDimension())
            config.setDisplayWindowMaximize("true");
        config.saveConfFile();
        if(FunctionTreeFactory.clearContent(getExtracts(), getIndex().getEditorList())) {
            Platform.exit();
            System.exit(0);
        }
    }

    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/Root.fxml"));
            rootLayout = loader.load();

            menuController = loader.getController();
            menuController.setMainApp(this);

            scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            loadCombinason();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void showWriter() {
        try {
            FXMLLoader loader = new CustomFXMLLoader(MainApp.class.getResource("fxml/Index.fxml"));
            AnchorPane writerLayout = loader.load();

            rootLayout.setCenter(writerLayout);


            MdTextController controller = loader.getController();
            controller.setMainApp(this);
            Index = controller;

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }



    public MenuController getMenuController() {
        return menuController;
    }

    public void initConnection(){
        if(!config.getAuthentificationUsername().isEmpty() && !config.getAuthentificationPassword().isEmpty()){
            LoginService loginTask = new LoginService(config.getAuthentificationUsername(), config.getAuthentificationPassword());

            menuController.getMenuDownload().setDisable(true);
            menuController.gethBottomBox().getChildren().clear();
            menuController.gethBottomBox().getChildren().addAll(menuController.getLabelField());
            menuController.getLabelField().textProperty().bind(loginTask.messageProperty());

            loginTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
                Alert alert = new CustomAlert(Alert.AlertType.NONE);
                alert.setTitle(Configuration.bundle.getString("ui.dialog.auth.title"));
                alert.setHeaderText(Configuration.bundle.getString("ui.dialog.auth.state.header"));


                switch(newValue){
                    case FAILED:
                    case CANCELLED:
                        alert.setAlertType(Alert.AlertType.ERROR);
                        alert.setContentText(Configuration.bundle.getString("ui.dialog.auth.failed.text"));

                        alert.showAndWait();
                        menuController.getMenuDownload().setDisable(false);
                        menuController.gethBottomBox().getChildren().clear();

                        break;
                    case SUCCEEDED:
                        menuController.getMenuDownload().setDisable(false);
                        break;
                }
            });

            loginTask.start();
        }
    }

    private void loadCombinason() {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, t -> {
            String codeStr = t.getCode().toString();
            if(!key.toString().endsWith("_"+codeStr)){
                 key.append("_").append(codeStr);
            }
        });
        scene.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent t) {
                if(key.length()>0) {
                    if(key.toString().equals("_CONTROL_C_L_E_M")){
                     // Create the custom dialog.
                        Dialog<Void> dialog = new Dialog<>();
                        dialog.setTitle("Easter Egg");
                        dialog.setHeaderText(null);
                        dialog.setContentText(null);

                        dialog.setGraphic(new ImageView(this.getClass().getResource("assets/static/goal.gif").toString()));
                        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);

                        dialog.showAndWait();
                    }
                    key = new StringBuilder();
                }

            }
        });
    }
}
