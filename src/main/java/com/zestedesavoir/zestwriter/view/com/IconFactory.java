package com.zestedesavoir.zestwriter.view.com;

import com.zestedesavoir.zestwriter.MainApp;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
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

    public static MaterialDesignIconView createLinkIcon() {
        return makeIcon(MaterialDesignIcon.LINK, "#48a200");
    }

    public static MaterialDesignIconView createCodeIcon() {
        return makeIcon(MaterialDesignIcon.CODE_TAGS, "#48a200");
    }

    public static MaterialDesignIconView createContentIcon(String type) {
        MaterialDesignIconView icon;
        if(type.equalsIgnoreCase("ARTICLE")) {
            icon = makeIcon(MaterialDesignIcon.BOOK_OPEN, "#ef9708");
        } else {
            icon = makeIcon(MaterialDesignIcon.LIBRARY_BOOKS, "#48a200");
        }
        icon.setSize("2em");
        return icon;
    }

    public static void addAlertLogo(Alert alert){
        Stage alertDialog = (Stage)alert.getDialogPane().getScene().getWindow();
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
        alertDialog.getIcons().add(new Image(MainApp.class.getResourceAsStream("assets/static/icons/logo.png")));
    }
}
