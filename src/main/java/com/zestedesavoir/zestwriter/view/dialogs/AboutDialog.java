package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.MainApp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AboutDialog{
    private MainApp mainApp;
    @FXML private Label version;

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
        version.setText(mainApp.getConfig().getProps().getProperty("version", "Inconnue"));
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
