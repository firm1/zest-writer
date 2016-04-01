package com.zestedesavoir.zestwriter.view.com;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

public class IconFactory {
	public static MaterialDesignIconView createFolderIcon() {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.FOLDER_MULTIPLE);
        icon.setSize("1.8em");
        icon.setGlyphStyle("-fx-fill:#084561");
        return icon;
    }

    public static MaterialDesignIconView createAddFolderIcon() {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.FOLDER_PLUS);
        icon.setSize("1.8em");
        icon.setGlyphStyle("-fx-fill:#084561");
        return icon;
    }

    public static MaterialDesignIconView createDeleteIcon() {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.CLOSE);
        icon.setSize("1.8em");
        icon.setGlyphStyle("-fx-fill:#f44336");
        return icon;
    }

    public static MaterialDesignIconView createRemoveIcon() {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.DELETE);
        icon.setSize("1.8em");
        icon.setGlyphStyle("-fx-fill:#f44336");
        return icon;
    }

    public static MaterialDesignIconView createFileIcon() {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.FILE);
        icon.setSize("1.8em");
        icon.setGlyphStyle("-fx-fill:#ef9708");
        return icon;
    }

    public static MaterialDesignIconView createEditIcon() {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.BORDER_COLOR);
        icon.setSize("1.8em");
        icon.setGlyphStyle("-fx-fill:#084561");
        return icon;
    }

    public static MaterialDesignIconView createGoogleIcon() {
        MaterialDesignIconView icon = new MaterialDesignIconView(MaterialDesignIcon.GOOGLE_PLUS);
        icon.setSize("2em");
        icon.setGlyphStyle("-fx-fill:#dd4b39");
        return icon;
    }
}
