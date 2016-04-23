package com.zestedesavoir.zestwriter.view.com;

import com.zestedesavoir.zestwriter.MainApp;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class IconFactory {
    public static MaterialDesignIconView makeIcon(MaterialDesignIcon type, String hexColor) {
        MaterialDesignIconView icon = new MaterialDesignIconView(type);
        icon.setSize("1.8em");
        icon.setGlyphStyle("-fx-fill:"+hexColor);
        return icon;
    }

    public static MaterialDesignIconView createLoginIcon() {
        return makeIcon(MaterialDesignIcon.ACCOUNT_KEY, "#084561");
    }

	public static MaterialDesignIconView createFolderIcon() {
	    return makeIcon(MaterialDesignIcon.FOLDER_MULTIPLE, "#084561");
    }

    public static MaterialDesignIconView createAddFolderIcon() {
        return makeIcon(MaterialDesignIcon.FOLDER_PLUS, "#084561");
    }

    public static MaterialDesignIconView createDeleteIcon() {
        return makeIcon(MaterialDesignIcon.CLOSE, "#f44336");
    }

    public static MaterialDesignIconView createRemoveIcon() {
        return makeIcon(MaterialDesignIcon.CLOSE, "#f44336");
    }

    public static MaterialDesignIconView createFileIcon() {
        return makeIcon(MaterialDesignIcon.FILE, "#ef9708");
    }

    public static MaterialDesignIconView createFileBlankIcon() {
        return makeIcon(MaterialDesignIcon.FILE_OUTLINE, "#ef9708");
    }

    public static MaterialDesignIconView createEditIcon() {
        return makeIcon(MaterialDesignIcon.BORDER_COLOR, "#084561");
    }

    public static MaterialDesignIconView createGoogleIcon() {
        return makeIcon(MaterialDesignIcon.GOOGLE_PLUS, "#dd4b39");
    }

    public static MaterialDesignIconView createArrowDownIcon() {
        return makeIcon(MaterialDesignIcon.ARROW_DOWN, "#48a200");
    }

    public static void addAlertLogo(Alert alert){
        Stage alertDialog = (Stage)alert.getDialogPane().getScene().getWindow();
        alertDialog.getIcons().add(new Image(MainApp.class.getResourceAsStream("assets/static/icons/logo.png")));
    }
}
