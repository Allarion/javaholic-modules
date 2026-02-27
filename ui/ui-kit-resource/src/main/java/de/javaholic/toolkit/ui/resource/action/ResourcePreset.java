package de.javaholic.toolkit.ui.resource.action;

/**
 * Defines which default Resource actions are enabled.
 *
 * <p>Presets affect only default create/edit/delete actions. Custom actions can be added
 * independently of any preset.</p>
 */
public interface ResourcePreset {

    boolean enableCreate();

    boolean enableEdit();

    boolean enableDelete();
}
// FIXME: 1) NO boolean Flags rather list of actions. 2) Presets are questionable conceptionally.
