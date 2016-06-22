package com.zestedesavoir.zestwriter.view.com;

import javafx.scene.control.Dialog;

public class CustomDialog<T> extends Dialog<T> {

    public CustomDialog() {
        super();
        FunctionTreeFactory.addTheming(this.getDialogPane());
    }
}
