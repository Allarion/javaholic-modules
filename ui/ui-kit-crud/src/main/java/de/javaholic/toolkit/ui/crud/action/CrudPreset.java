package de.javaholic.toolkit.ui.crud.action;

/**
 * Defines which default CRUD actions are enabled.
 *
 * <p>Presets affect only default create/edit/delete actions. Custom actions can be added
 * independently of any preset.</p>
 */
public interface CrudPreset {

    boolean enableCreate();

    boolean enableEdit();

    boolean enableDelete();
}
