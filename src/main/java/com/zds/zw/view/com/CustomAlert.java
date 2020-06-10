package com.zds.zw.view.com;

import com.zds.zw.MainApp;
import javafx.beans.NamedArg;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

public class CustomAlert extends Alert{
    public CustomAlert(@NamedArg("alertType") AlertType alertType) {
        super(alertType);
        initCustomize();
    }

    public CustomAlert(@NamedArg("alertType") AlertType alertType, @NamedArg("contentText") String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
        initCustomize();
    }

    private void initCustomize() {
        IconFactory.addAlertLogo(this);
        FunctionTreeFactory.addTheming(this.getDialogPane());
        initModality(Modality.APPLICATION_MODAL);
        initOwner(MainApp.getPrimaryStage());
    }
}
