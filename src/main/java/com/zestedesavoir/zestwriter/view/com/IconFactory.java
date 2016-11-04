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
    public static MaterialDesignIconView makeIcon(MaterialDesignIcon type, String cssClass) {
        MaterialDesignIconView icon = new MaterialDesignIconView(type);
        icon.setSize("1.8em");
        icon.setStyleClass(cssClass);
        return icon;
    }

    public static MaterialDesignIconView createLoginIcon() {
        return makeIcon(MaterialDesignIcon.ACCOUNT_KEY, "icon-account-key"); //084561
    }

	public static MaterialDesignIconView createFolderIcon() {
	    return makeIcon(MaterialDesignIcon.FOLDER_MULTIPLE, "icon-folder-multiple"); //084561
    }

    public static MaterialDesignIconView createAddFolderIcon() {
        return makeIcon(MaterialDesignIcon.FOLDER_PLUS, "icon-folder-plus"); //084561
    }

    public static MaterialDesignIconView createDeleteIcon() {
        return makeIcon(MaterialDesignIcon.CLOSE, "icon-close"); //f44336
    }

    public static MaterialDesignIconView createRemoveIcon() {
        return makeIcon(MaterialDesignIcon.CLOSE, "icon-close"); //f44336
    }

    public static MaterialDesignIconView createFileIcon() {
        return makeIcon(MaterialDesignIcon.FILE, "icon-file"); //ef9708
    }

    public static MaterialDesignIconView createFileBlankIcon() {
        return makeIcon(MaterialDesignIcon.FILE_OUTLINE, "icon-file-outline"); //ef9708
    }

    public static MaterialDesignIconView createEditIcon() {
        return makeIcon(MaterialDesignIcon.BORDER_COLOR, "icon-border-color"); //084561
    }

    public static MaterialDesignIconView createGoogleIcon() {
        return makeIcon(MaterialDesignIcon.GOOGLE_PLUS, "icon-google-plus"); //dd4b39
    }

    public static MaterialDesignIconView createArrowDownIcon() {
        return makeIcon(MaterialDesignIcon.ARROW_DOWN, "icon-arrow-down"); //48a200
    }

    public static MaterialDesignIconView createStatsIcon() {
        return makeIcon(MaterialDesignIcon.CHART_AREASPLINE, "icon-chart-areaspline"); //48a200
    }

    public static MaterialDesignIconView createStatsHistoIcon() {
        return makeIcon(MaterialDesignIcon.CHART_BAR, "icon-chart-bar"); //48a200
    }

    public static MaterialDesignIconView createStatsPieIcon() {
        return makeIcon(MaterialDesignIcon.CHART_PIE, "icon-chart-pie"); //48a200
    }

    public static MaterialDesignIconView createLinkIcon() {
        return makeIcon(MaterialDesignIcon.LINK, "icon-link"); //48a200
    }

    public static MaterialDesignIconView createCodeIcon() {
        return makeIcon(MaterialDesignIcon.CODE_TAGS, "icon-code-tags"); //48a200
    }

    public static MaterialDesignIconView createArticleIcon() {
        return makeIcon(MaterialDesignIcon.BOOK_OPEN, "icon-book-open"); //ef9708
    }

    public static MaterialDesignIconView createTutorialIcon() {
        return makeIcon(MaterialDesignIcon.LIBRARY_BOOKS, "icon-library-books"); //48a200
    }

    public static MaterialDesignIconView createAbcIcon() {
        return makeIcon(MaterialDesignIcon.SPELLCHECK, "icon-correction"); //48a200
    }

    public static MaterialDesignIconView createContentIcon(String type) {
        MaterialDesignIconView icon;
        if(type.equalsIgnoreCase("ARTICLE")) {
            icon = makeIcon(MaterialDesignIcon.BOOK_OPEN, "icon-book-open"); //ef9708
        } else {
            icon = makeIcon(MaterialDesignIcon.LIBRARY_BOOKS, "icon-library-books"); //48a200
        }
        icon.setSize("2em");
        return icon;
    }

    public static void addAlertLogo(Alert alert){
        Stage alertDialog = (Stage)alert.getDialogPane().getScene().getWindow();
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
        alertDialog.getIcons().add(new Image(MainApp.class.getResourceAsStream("images/logo.png")));
    }
}
