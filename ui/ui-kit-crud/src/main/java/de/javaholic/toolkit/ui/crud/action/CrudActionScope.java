package de.javaholic.toolkit.ui.crud.action;

/**
 * Defines where a CRUD action is rendered and what context it operates on.
 */
public enum CrudActionScope {
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
