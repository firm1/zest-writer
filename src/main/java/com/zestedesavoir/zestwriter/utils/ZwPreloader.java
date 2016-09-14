package com.zestedesavoir.zestwriter.utils;

import com.zestedesavoir.zestwriter.MainApp;
import javafx.application.Preloader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZwPreloader extends Preloader{
    private static Logger logger = LoggerFactory.getLogger(ZwPreloader.class);
    private static long startTime;
    private static long loaderTime;
    private static long endTime;
    private static Stage preloaderStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        startTime = System.currentTimeMillis();
        preloaderStage = primaryStage;

        Group root = new Group();
        root.getChildren().add(new ImageView(MainApp.class.getResource("assets/static/images/splash.png").toString()));

        Label zwLabel = new Label("Zest Writer 1.3.0");
        Label loadingLabel = new Label("Chargement en cours...");
        zwLabel.setFont(new Font(zwLabel.getFont().getName(), 16));
        loadingLabel.setFont(new Font(zwLabel.getFont().getName(), 16));
        zwLabel.setLayoutX(20);
        loadingLabel.setLayoutX(20);
        zwLabel.setLayoutY(190);
        loadingLabel.setLayoutY(210);

        root.getChildren().add(zwLabel);
        root.getChildren().add(loadingLabel);

        Scene scene = new Scene(root, 400, 250);

        preloaderStage.setScene(scene);
        preloaderStage.setResizable(false);
        preloaderStage.setTitle("Zest Writer");
        preloaderStage.initStyle(StageStyle.TRANSPARENT);
        preloaderStage.show();
        loaderTime = System.currentTimeMillis();
    }

    public static void closePreloader(){
        preloaderStage.close();
        endTime = System.currentTimeMillis();
        logger.debug("Time for showing preloader: " + (loaderTime - startTime) + "ms");
        logger.info("Starting time: " + (endTime - startTime) + "ms");
        logger.debug("Prelodaer showed while " + (endTime - loaderTime) + "ms");
    }
}
