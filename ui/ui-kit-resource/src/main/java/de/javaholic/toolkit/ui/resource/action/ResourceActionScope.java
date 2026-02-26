package de.javaholic.toolkit.ui.resource.action;

/**
 * Defines where a Resource action is rendered and what context it operates on.
 */
public enum ResourceActionScope {
    /**
     * Toolbar action above the grid, independent of one specific item.
     */
    TOOLBAR,
    /**
     * Action rendered per row in the actions column, operating on one item.
     */
    ROW,
    /**
     * Toolbar action operating on the current grid selection.
     */
    SELECTION
}

