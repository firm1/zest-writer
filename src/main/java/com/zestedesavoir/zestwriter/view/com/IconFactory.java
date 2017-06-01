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
    private IconFactory() {
    }

    /**
     * Create icon for whatever, sized for zest writer menu
     * @param type code name of material design icon
     * @param cssClass css class for override standard
     * @return material icon
     */
    public static MaterialDesignIconView makeIcon(MaterialDesignIcon type, String cssClass) {
        MaterialDesignIconView icon = new MaterialDesignIconView(type);
        icon.setSize("1.8em");
        icon.setStyleClass(cssClass);
        return icon;
    }

    /**
     * Create icon for login
     * @return login's material icon
     */
    public static MaterialDesignIconView createLoginIcon() {
        return makeIcon(MaterialDesignIcon.ACCOUNT_KEY, "icon-account-key"); //084561
    }

    /**
     * Create icon for container
     * @return container material icon
     */
	public static MaterialDesignIconView createFolderIcon() {
	    return makeIcon(MaterialDesignIcon.FOLDER_MULTIPLE, "icon-folder-multiple"); //084561
    }

    /**
     * Create icon for add container
     * @return material icon for new container
     */
    public static MaterialDesignIconView createAddFolderIcon() {
        return makeIcon(MaterialDesignIcon.FOLDER_PLUS, "icon-folder-plus"); //084561
    }

    /**
     * Create icon for delete something
     * @return delete material icon
     */
    public static MaterialDesignIconView createDeleteIcon() {
        return makeIcon(MaterialDesignIcon.CLOSE, "icon-close"); //f44336
    }

    /**
     * Create icon for remove something
     * @return remove material icon
     */
    public static MaterialDesignIconView createRemoveIcon() {
        return makeIcon(MaterialDesignIcon.CLOSE, "icon-close"); //f44336
    }

    /**
     * Create icon for move
     * @return move material icon
     */
    public static MaterialDesignIconView createMoveIcon() {
        return makeIcon(MaterialDesignIcon.FOLDER_MOVE, "icon-move"); //f44336
    }

    /**
     * Create icon for fill file
     * @return file material icon
     */
    public static MaterialDesignIconView createFileIcon() {
        return makeIcon(MaterialDesignIcon.FILE, "icon-file"); //ef9708
    }

    /**
     * Create icon for simple file
     * @return file material icon
     */
    public static MaterialDesignIconView createFileBlankIcon() {
        return makeIcon(MaterialDesignIcon.FILE_OUTLINE, "icon-file-outline"); //ef9708
    }

    /**
     * Create icon for edit something
     * @return edit material icon
     */
    public static MaterialDesignIconView createEditIcon() {
        return makeIcon(MaterialDesignIcon.BORDER_COLOR, "icon-border-color"); //084561
    }

    /**
     * Create icon for Google Plus
     * @return Google plus material icon
     */
    public static MaterialDesignIconView createGoogleIcon() {
        return makeIcon(MaterialDesignIcon.GOOGLE_PLUS, "icon-google-plus"); //dd4b39
    }

    /**
     * Create icon for arrow down
     * @return arrow down material icon
     */
    public static MaterialDesignIconView createArrowDownIcon() {
        return makeIcon(MaterialDesignIcon.ARROW_DOWN, "icon-arrow-down"); //48a200
    }

    /**
     * Create icon for line chart
     * @return line chart material icon
     */
    public static MaterialDesignIconView createStatsIcon() {
        return makeIcon(MaterialDesignIcon.CHART_AREASPLINE, "icon-chart-areaspline"); //48a200
    }

    /**
     * Create icon for histogram
     * @return histogram's material icon
     */
    public static MaterialDesignIconView createStatsHistoIcon() {
        return makeIcon(MaterialDesignIcon.CHART_BAR, "icon-chart-bar"); //48a200
    }

    /**
     * Create icon for link
     * @return link's material icon
     */
    public static MaterialDesignIconView createLinkIcon() {
        return makeIcon(MaterialDesignIcon.LINK, "icon-link"); //48a200
    }

    /**
     * Create icon for code
     * @return code's material icon
     */
    public static MaterialDesignIconView createCodeIcon() {
        return makeIcon(MaterialDesignIcon.CODE_TAGS, "icon-code-tags"); //48a200
    }

    /**
     * Create icon for articles
     * @return article's material icon
     */
    public static MaterialDesignIconView createArticleIcon() {
        return makeIcon(MaterialDesignIcon.BOOK_OPEN, "icon-book-open"); //ef9708
    }

    /**
     * Create icon for tutorials
     * @return tutorial's material icon
     */
    public static MaterialDesignIconView createTutorialIcon() {
        return makeIcon(MaterialDesignIcon.LIBRARY_BOOKS, "icon-library-books"); //48a200
    }

    /**
     * Create icon for tutorials
     * @return tutorial's material icon
     */
    public static MaterialDesignIconView createOpinionIcon() {
        return makeIcon(MaterialDesignIcon.BOOK, "icon-book");
    }

    /**
     * Create icon for spell checks
     * @return spell checks material icon
     */
    public static MaterialDesignIconView createAbcIcon() {
        return makeIcon(MaterialDesignIcon.SPELLCHECK, "icon-correction"); //48a200
    }

    /**
     * Create material design icon for content (Tutorial or Article)
     * @param type content type for icon
     * @return Content Material Icon
     */
    public static MaterialDesignIconView createContentIcon(String type) {
        MaterialDesignIconView icon;
        if("ARTICLE".equalsIgnoreCase(type)) {
            icon = createArticleIcon();
        } else if("OPINION".equalsIgnoreCase(type)) {
            icon = createOpinionIcon();
        } else {
            icon = createTutorialIcon();
        }
        icon.setSize("2em");
        return icon;
    }

    /**
     * Add Zest Writer logo on Alert
     * @param alert alert on which to add the icon
     */
    public static void addAlertLogo(Alert alert){
        Stage alertDialog = (Stage)alert.getDialogPane().getScene().getWindow();
        alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label)node).setMinHeight(Region.USE_PREF_SIZE));
        alertDialog.getIcons().add(new Image(MainApp.class.getResourceAsStream("images/logo.png")));
    }
}
