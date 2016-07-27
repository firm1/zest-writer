package com.zestedesavoir.zestwriter.model;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

public interface ContentNode {
    String getFilePath();
    String getTitle();
    void setTitle(String title);
    MaterialDesignIconView buildIcon();
    default boolean canDelete() {
        return false;
    }
    default boolean isMoveableIn(ContentNode receiver, Content root) {
        return false;
    }
    default boolean canTakeContainer(Content c) { return false; }
    default boolean canTakeExtract() {
        return false;
    }

    default boolean isEditable() {
        return true;
    }
    default void delete() {}
    default void renameTitle(String title) {
        setTitle(title);
    }
}
