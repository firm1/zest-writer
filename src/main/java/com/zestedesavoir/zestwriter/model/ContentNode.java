package com.zestedesavoir.zestwriter.model;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

public interface ContentNode {
    public String getFilePath();
    public String getTitle();
    public void setTitle(String title);
    public MaterialDesignIconView buildIcon();
    public boolean canDelete();
    public void delete();
    public boolean isMoveableIn(ContentNode receiver, Content root);
    public boolean canTakeContainer(Content c);
    public boolean canTakeExtract();
    public boolean isEditable();
}
