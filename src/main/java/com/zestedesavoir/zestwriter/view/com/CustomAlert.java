package com.zestedesavoir.zestwriter.view.com;

import com.zestedesavoir.zestwriter.utils.Configuration;
import javafx.beans.NamedArg;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;

public class CustomAlert extends Alert{

    public CustomAlert(@NamedArg("alertType") AlertType alertType) {
        super(alertType);
        IconFactory.addAlertLogo(this);
        FunctionTreeFactory.addTheming(this.getDialogPane());
    }

    public CustomAlert(@NamedArg("alertType") AlertType alertType, @NamedArg("contentText") String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
        IconFactory.addAlertLogo(this);
        FunctionTreeFactory.addTheming(this.getDialogPane());
    }

}
