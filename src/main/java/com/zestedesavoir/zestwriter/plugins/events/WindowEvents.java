package com.zestedesavoir.zestwriter.plugins.events;


public interface WindowEvents{
    /**
     * Redimensionnement en largeur de la fenêtre principale
     * @param oldValue Ancienne valeur
     * @param newValue Nouvelle valeur
     */
    default void WindowWidthResizeEvent(double oldValue, double newValue){}

    /**
     * Redimensionnement en hauteur de la fenêtre principale
     * @param oldValue Ancienne valeur
     * @param newValue Nouvelle valeur
     */
    default void WindowHeightResizeEvent(int oldValue, int newValue){}

    /**
     * Quand le fenêtre principale est fermé (Fermeture du programme)
     */
    default void WindowCloseEvent(){}

    /**
     * Quand la fenêtre change l'état "Maximisé"
     * @param oldValue Ancienne valeur
     * @param newValue Nouvelle valeur
     */
    default void WindowMaximizedChangeEvent(boolean oldValue, boolean newValue){}

    /**
     * Quand le focus de la fenêtre change
     * @param oldValue Ancienne valeur
     * @param newValue Nouvelle valeur
     */
    default void WindowFocusChangeEvent(boolean oldValue, boolean newValue){}
}
