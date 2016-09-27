package com.zestedesavoir.zestwriter.model;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

public interface ContentNode {
    String getFilePath();
    String getTitle();
    void setTitle(String title);
    MaterialDesignIconView buildIcon();
    boolean canDelete();
    boolean isMoveableIn(ContentNode receiver, Content root);
    boolean canTakeContainer(Content c);
    boolean canTakeExtract();

    boolean isEditable();
    void delete();
    void renameTitle(String title);
}
