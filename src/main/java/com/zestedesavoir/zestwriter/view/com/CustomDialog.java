package com.zestedesavoir.zestwriter.view.com;

import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

public class CustomDialog<T> extends Dialog<T> {

    public CustomDialog() {
        super();
        FunctionTreeFactory.addTheming(this.getDialogPane());
        getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
    }
}
