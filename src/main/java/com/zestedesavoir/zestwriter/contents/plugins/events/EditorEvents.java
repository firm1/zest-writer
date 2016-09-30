package com.zestedesavoir.zestwriter.contents.plugins.events;


public interface EditorEvents{
    /**
     * When caret position are changed
     * @param oldPosition Old value
     * @param newPosition New value
     */
    default void onEditorPositionChange(int oldPosition, int newPosition){}

    /**
     * When text in textarea are changed
     * @param oldText Old value
     * @param newText New Value
     */
    default void onEditorTextChange(String oldText, String newText){}
}
