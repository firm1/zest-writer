package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.MainApp;
import javafx.fxml.FXML;

public class AboutDialog{
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }

    @FXML
    private void HandleGplHyperlinkAction(){
        mainApp.getHostServices().showDocument("https://github.com/firm1/zest-writer/blob/master/LICENSE");
    }

    @FXML
    private void HandleSourceHyperlinkAction(){
        mainApp.getHostServices().showDocument("https://github.com/firm1/zest-writer");
    }

    @FXML
    private void HandleZdsHyperlinkAction(){
        mainApp.getHostServices().showDocument("https://zestedesavoir.com/");
    }
}
