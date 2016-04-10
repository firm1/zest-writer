package com.zestedesavoir.zestwriter.view.dialogs;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zestedesavoir.zestwriter.MainApp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AboutDialog{
    private MainApp mainApp;
    private Logger logger;
    @FXML private Label version;


    public AboutDialog() {
        logger = LoggerFactory.getLogger(AboutDialog.class);
    }

    @FXML private void initialize() {
        Properties props = new Properties();
        try {
            InputStream input = new FileInputStream("gradle.properties");
            props.load(input);
            version.setText(props.getProperty("version", "Inconnue"));
            input.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    @FXML private void HandleGplHyperlinkAction(){
        mainApp.getHostServices().showDocument("https://github.com/firm1/zest-writer/blob/master/LICENSE");
    }

    @FXML private void HandleSourceHyperlinkAction(){
        mainApp.getHostServices().showDocument("https://github.com/firm1/zest-writer");
    }

    @FXML private void HandleZdsHyperlinkAction(){
        mainApp.getHostServices().showDocument("https://zestedesavoir.com/");
    }
}
