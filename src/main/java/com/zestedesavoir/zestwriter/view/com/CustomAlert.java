package com.zestedesavoir.zestwriter.view.com;

import com.zestedesavoir.zestwriter.MainApp;
import javafx.beans.NamedArg;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CustomAlert extends Alert{

    public CustomAlert(@NamedArg("alertType") AlertType alertType) {
        super(alertType);
        initCustomize();
    }

    public CustomAlert(@NamedArg("alertType") AlertType alertType, @NamedArg("contentText") String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
        initCustomize();

    }

    public void initCustomize() {
        IconFactory.addAlertLogo(this);
        FunctionTreeFactory.addTheming(this.getDialogPane());
        initModality(Modality.APPLICATION_MODAL);
        initOwner(MainApp.getPrimaryStage());
    }

}
