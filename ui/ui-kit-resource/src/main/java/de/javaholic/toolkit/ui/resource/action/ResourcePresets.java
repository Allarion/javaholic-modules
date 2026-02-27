package de.javaholic.toolkit.ui.resource.action;

/**
 * Factory for commonly used default-action presets.
 *
 * <p>Presets configure default create/edit/delete UI only. Custom actions can always be
 * added and rendered regardless of preset choice. {@link #none()} hides all default
 * create/edit/delete actions while keeping the grid and optional form support available.</p>
 */
@Deprecated
public final class ResourcePresets {

    private static final ResourcePreset FULL = new FixedResourcePreset(true, true, true);
    private static final ResourcePreset READ_ONLY = new FixedResourcePreset(false, false, false);
    private static final ResourcePreset NONE = new FixedResourcePreset(false, false, false);

    private ResourcePresets() {
    }

    /**
     * Enables default create, edit and delete actions.
     */
    public static ResourcePreset full() {
        return FULL;
    }

    /**
     * Disables default create, edit and delete actions.
     */
    public static ResourcePreset readOnly() {
        return READ_ONLY;
    }

    /**
     * Disables all default create, edit and delete UI.
     */
    public static ResourcePreset none() {
        return NONE;
    }

    private record FixedResourcePreset(boolean enableCreate, boolean enableEdit, boolean enableDelete) implements ResourcePreset {
    }
}

