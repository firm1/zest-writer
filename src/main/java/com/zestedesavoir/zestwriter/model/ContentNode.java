package com.zestedesavoir.zestwriter.model;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

public interface ContentNode {
    String getFilePath();
    String getTitle();
    void setTitle(String title);
    MaterialDesignIconView buildIcon();
    boolean canDelete();
    void delete();
    boolean isMoveableIn(ContentNode receiver, Content root);
    boolean canTakeContainer(Content c);
    boolean canTakeExtract();
    boolean isEditable();
}
