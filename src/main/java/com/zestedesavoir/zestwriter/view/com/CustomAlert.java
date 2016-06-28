package com.zestedesavoir.zestwriter.view.com;

import javafx.beans.NamedArg;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CustomAlert extends Alert{

    public CustomAlert(@NamedArg("alertType") AlertType alertType, Stage owner) {
        super(alertType);
        IconFactory.addAlertLogo(this);
        FunctionTreeFactory.addTheming(this.getDialogPane());
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
    }

    public CustomAlert(@NamedArg("alertType") AlertType alertType, @NamedArg("contentText") String contentText, Stage owner, ButtonType... buttons) {
        super(alertType, contentText, buttons);
        IconFactory.addAlertLogo(this);
        FunctionTreeFactory.addTheming(this.getDialogPane());
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
    }

}
