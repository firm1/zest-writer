package com.zestedesavoir.zestwriter.plugins.events;


public interface WindowEvents{
    /**
     * Redimensionnement en largeur de la fenêtre principale
     * @param oldValue Ancienne valeur
     * @param newValue Nouvelle valeur
     */
    default void onWindowWidthResizeEvent(int oldValue, int newValue){}

    /**
     * Redimensionnement en hauteur de la fenêtre principale
     * @param oldValue Ancienne valeur
     * @param newValue Nouvelle valeur
     */
    default void onWindowHeightResizeEvent(int oldValue, int newValue){}

    /**
     * Quand le fenêtre principale est fermé (Fermeture du programme)
     */
    default void onWindowCloseEvent(){}

    /**
     * Quand la fenêtre change l'état "Maximisé"
     * @param oldValue Ancienne valeur
     * @param newValue Nouvelle valeur
     */
    default void onWindowMaximizedChangeEvent(boolean oldValue, boolean newValue){}

    /**
     * Quand le focus de la fenêtre change
     * @param oldValue Ancienne valeur
     * @param newValue Nouvelle valeur
     */
    default void onWindowFocusChangeEvent(boolean oldValue, boolean newValue){}
}
