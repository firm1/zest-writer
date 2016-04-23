package com.zestedesavoir.zestwriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import com.zestedesavoir.zestwriter.model.Content;
import com.zestedesavoir.zestwriter.model.Textual;
import com.zestedesavoir.zestwriter.utils.Configuration;
import com.zestedesavoir.zestwriter.utils.ZdsHttp;
import com.zestedesavoir.zestwriter.view.MdTextController;
import com.zestedesavoir.zestwriter.view.MenuController;

import com.zestedesavoir.zestwriter.view.com.IconFactory;
import com.zestedesavoir.zestwriter.view.task.LoginService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Service;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
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
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainApp extends Application {
    private Scene scene;
    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableMap<Textual, Tab> extracts = FXCollections.observableMap(new HashMap<>());
    private ObservableList<Content> contents = FXCollections.observableArrayList();
    private ZdsHttp zdsutils;
    private MdTextController Index;
    private Configuration config;
    private StringBuilder key = new StringBuilder();
    private Logger logger;
    private MenuController menuController;
    public static String[] args;

    public MainApp() {
        super();
        logger = LoggerFactory.getLogger(MenuController.class);

        if(args.length > 0) {
            config = new Configuration(args[0]);
        } else {
            config = new Configuration(System.getProperty("user.home"));
        }
        zdsutils = new ZdsHttp(config);
    }

    public static void main(String[] args) {
        MainApp.args  = args;
        launch(args);
    }


    public Configuration getConfig() {
        return config;
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

    public ObservableList<Content> getContents() {
        return contents;
    }

    public ZdsHttp getZdsutils() {
        return zdsutils;
    }

    public ObservableMap<Textual, Tab> getExtracts() {
        return extracts;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Zest Writer");
        this.primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("assets/static/icons/logo.png")));
        primaryStage.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });

        initRootLayout();
        showWriter();
        initConnection();
    }

    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("fxml/Root.fxml"));
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
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("fxml/Index.fxml"));
            AnchorPane writerLayout = loader.load();

            rootLayout.setCenter(writerLayout);


            MdTextController controller = loader.getController();
            controller.setMainApp(this);
            Index = controller;

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void initConnection(){
        if(!config.getAuthentificationUsername().isEmpty() && !config.getAuthentificationPassword().isEmpty()){
            LoginService loginTask = new LoginService(config.getAuthentificationUsername(), config.getAuthentificationPassword(), zdsutils, config);

            menuController.getMenuDownload().setDisable(true);
            menuController.gethBottomBox().getChildren().addAll(menuController.getLabelField());
            menuController.getLabelField().textProperty().bind(loginTask.messageProperty());

            loginTask.stateProperty().addListener((ObservableValue<? extends Worker.State> observableValue, Worker.State oldValue, Worker.State newValue) -> {
                Alert alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle("Connexion");
                alert.setHeaderText("Etat de connexion");

                IconFactory.addAlertLogo(alert);

                switch(newValue){
                    case FAILED:
                    case CANCELLED:
                        alert.setAlertType(Alert.AlertType.ERROR);
                        alert.setContentText("Désolé mais vous n'avez pas été authentifié sur le serveur de Zeste de Savoir.");

                        alert.showAndWait();
                        menuController.getMenuDownload().setDisable(false);

                        break;
                    case SUCCEEDED:
                        menuController.getMenuDownload().setDisable(false);
                        break;
                }
                menuController.gethBottomBox().getChildren().clear();
            });

            loginTask.start();
        }
    }

    private void loadCombinason() {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, t -> {
            String codeStr = t.getCode().toString();
            if(!key.toString().endsWith("_"+codeStr)){
                 key.append("_"+codeStr);
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
