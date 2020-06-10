package com.zds.zw.view.dialogs;


import com.zds.zw.MainApp;
import com.zds.zw.utils.Configuration;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AboutDialog{
    private MainApp mainApp;
    @FXML private Label version;

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
        version.setText(MainApp.getConfig().getProps().getProperty("version", Configuration.getBundle().getString("ui.version.label.unknown")));
    }

    @FXML private void handleGplHyperlinkAction(){
        mainApp.getHostServices().showDocument("https://github.com/firm1/zest-writer/blob/master/LICENSE");
    }

    @FXML private void handleSourceHyperlinkAction(){
        mainApp.getHostServices().showDocument("https://github.com/firm1/zest-writer");
    }

    @FXML private void handleZdsHyperlinkAction(){
        mainApp.getHostServices().showDocument("https://zestedesavoir.com/");
    }
}
