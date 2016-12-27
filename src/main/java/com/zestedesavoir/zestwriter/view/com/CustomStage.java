package com.zestedesavoir.zestwriter.view.com;

import com.zestedesavoir.zestwriter.MainApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomStage extends Stage{
    public CustomStage(FXMLLoader loader, String title) {
        super();
        setTitle(title);

        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (IOException e) {
            MainApp.getLogger().error(e.getMessage(), e);
        }
        setScene(scene);
        getIcons().add(new Image(MainApp.class.getResourceAsStream("images/logo.png")));
        initModality(Modality.APPLICATION_MODAL);
        initOwner(MainApp.getPrimaryStage());
    }

    public CustomStage(String title){
        super();
        setTitle(title);

        getIcons().add(new Image(MainApp.class.getResourceAsStream("images/logo.png")));
        initModality(Modality.APPLICATION_MODAL);
    }
}
