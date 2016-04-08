package com.zestedesavoir.zestwriter.view.dialogs;


import com.zestedesavoir.zestwriter.MainApp;
import javafx.fxml.FXML;

public class AboutDialog{
    @FXML
    private void HandleGplHyperlinkAction(){
        new MainApp().getHostServices().showDocument("https://github.com/firm1/zest-writer/blob/master/LICENSE");
    }

    @FXML
    private void HandleSourceHyperlinkAction(){
        new MainApp().getHostServices().showDocument("https://github.com/firm1/zest-writer");
    }

    @FXML
    private void HandleZdsHyperlinkAction(){
        new MainApp().getHostServices().showDocument("https://zestedesavoir.com/");
    }
}
