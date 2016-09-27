package com.zestedesavoir.zestwriter.model;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

public interface ContentNode {
    String getFilePath();
    String getTitle();
    void setTitle(String title);
    MaterialDesignIconView buildIcon();
    boolean canDelete(); // { return false; }
    boolean isMoveableIn(ContentNode receiver, Content root); // { return false; }
    boolean canTakeContainer(Content c);// { return false; }
    boolean canTakeExtract();// { return false; }

    boolean isEditable(); // { return true; }
    void delete(); // {}
    void renameTitle(String title); // { setTitle(title); }
}
