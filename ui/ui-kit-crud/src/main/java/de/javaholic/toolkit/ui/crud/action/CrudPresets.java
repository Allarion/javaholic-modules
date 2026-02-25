package de.javaholic.toolkit.ui.crud.action;

/**
 * Factory for commonly used default-action presets.
 *
 * <p>Presets configure default create/edit/delete UI only. Custom actions can always be
 * added and rendered regardless of preset choice. {@link #none()} hides all default
 * create/edit/delete actions while keeping the grid and optional form support available.</p>
 */
public final class CrudPresets {

    private static final CrudPreset FULL = new FixedCrudPreset(true, true, true);
    private static final CrudPreset READ_ONLY = new FixedCrudPreset(false, false, false);
    private static final CrudPreset NONE = new FixedCrudPreset(false, false, false);

    private CrudPresets() {
    }

    /**
     * Enables default create, edit and delete actions.
     */
    public static CrudPreset full() {
        return FULL;
    }

    /**
     * Disables default create, edit and delete actions.
     */
    public static CrudPreset readOnly() {
        return READ_ONLY;
    }

    /**
     * Disables all default create, edit and delete UI.
     */
    public static CrudPreset none() {
        return NONE;
    }

    private record FixedCrudPreset(boolean enableCreate, boolean enableEdit, boolean enableDelete) implements CrudPreset {
    }
}
