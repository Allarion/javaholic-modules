package de.javaholic.toolkit.ui.policy;

/**
 * Immutable runtime decision derived from UI metadata.
 */
public final class UiDecision {

    private final boolean visible;
    private final boolean enabled;
    private final boolean readOnly;
    private final boolean required;

    private UiDecision(boolean visible, boolean enabled, boolean readOnly, boolean required) {
        this.visible = visible;
        this.enabled = enabled;
        this.readOnly = readOnly;
        this.required = required;
    }

    public static UiDecision visibleEnabled() {
        return new UiDecision(true, true, false, false);
    }

    public static UiDecision hidden() {
        return new UiDecision(false, false, false, false);
    }

    public static UiDecision visibleReadOnly() {
        return new UiDecision(true, false, true, false);
    }

    public static UiDecision custom(boolean visible, boolean enabled, boolean readOnly, boolean required) {
        return new UiDecision(visible, enabled, readOnly, required);
    }

    public boolean visible() {
        return visible;
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean readOnly() {
        return readOnly;
    }

    public boolean required() {
        return required;
    }
}
