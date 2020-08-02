package com.zds.zw.model;

import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;

/**
 * Interface which associated to any item what you can find on summary tree
 */
public interface ContentNode {
    /**
     * Get absolute filePath of node
     * @return absolute file path
     */
    String getFilePath();

    /**
     * Get node title
     * @return node title
     */
    String getTitle();

    /**
     * Edit node title
     * @param title
     */
    void setTitle(String title);

    /**
     * Build Material icon for node
     * @return material icon
     */
    MaterialDesignIconView buildIcon();

    /**
     * Check if all conditions for delete node is required
     * @return true if node is deletable, false else
     */
    default boolean canDelete() {
        return false;
    }

    /**
     * Check if all conditions for mode node on another node is required
     * @param receiver node which receive instance
     * @param root root node for optimize analysis
     * @return true is node is movable, false else
     */
    default boolean isMovableIn(ContentNode receiver, Content root) {
        return false;
    }

    /**
     * Check if all conditions for receive container is required
     * @param c global content
     * @return true if node can take container
     */
    default boolean canTakeContainer(Content c) { return false; }

    /**
     * check if all conditions for receive extract is required
     * @return true if node can take extract
     */
    default boolean canTakeExtract() {
        return false;
    }

    /**
     * check if node is editable (for example, introduction isn't editable)
     * @return true if editable, false else
     */
    default boolean isEditable() {
        return true;
    }

    /**
     * delete node physically
     */
    default void delete() {}

    /**
     * Rename title of node
     * @param title new title
     */
    default void renameTitle(String title) {
        setTitle(title);
    }

    /**
     * Check if node have and can merge children extracts
     * @param c global content
     * @return true if merge extracts is possible
     */
    default boolean canMergeExtracts(Content c) {return false; }
}
