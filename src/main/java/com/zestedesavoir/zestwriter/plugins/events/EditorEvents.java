package com.zestedesavoir.zestwriter.plugins.events;


public interface EditorEvents{
    /**
     * Lorsque la position du curseur change
     * @param oldPosition Ancienne valeur
     * @param newPosition Nouvelle valeur
     */
    default void onEditorPositionChange(int oldPosition, int newPosition){}

    /**
     * Lorsque le texte dans l'éditeur change
     * @param oldText Ancienne valeur
     * @param newText Nouvelle valeur
     */
    default void onEditorTextChange(String oldText, String newText){}
}
