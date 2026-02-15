package de.javaholic.toolkit.ui.meta;

import java.util.Objects;

/**
 * Mutable override config for one {@link UiProperty} semantic profile.
 *
 * <p>This type is intended for fluent auto-builder overrides (for example in CRUD auto builders)
 * and not for direct component rendering.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * UiPropertyConfig<User> cfg = new UiPropertyConfig<>(property)
 *     .label("user.email")
 *     .visible(true)
 *     .required(true)
 *     .tooltip("user.email.tooltip");
 * }</pre>
 */
public final class UiPropertyConfig<T> {

    private final UiProperty<T> property;
    private String labelKey;
    private boolean visible;
    private boolean required;
    private String tooltipKey;
    private boolean labelOverridden;
    private boolean visibleOverridden;
    private boolean requiredOverridden;
    private boolean tooltipOverridden;

    /**
     * Creates a fluent semantic config initialized from the source property defaults.
     *
     * <p>Example: {@code UiPropertyConfig<User> cfg = new UiPropertyConfig<>(property);}</p>
     */
    public UiPropertyConfig(UiProperty<T> property) {
        this.property = Objects.requireNonNull(property, "property");
        this.labelKey = property.labelKey();
        this.visible = property.isVisible();
    }

    /**
     * Overrides the semantic label key.
     *
     * <p>Example: {@code cfg.label("user.email.label");}</p>
     */
    public UiPropertyConfig<T> label(String key) {
        this.labelKey = Objects.requireNonNull(key, "key");
        this.labelOverridden = true;
        return this;
    }

    /**
     * Overrides semantic visibility.
     *
     * <p>Example: {@code cfg.visible(false);}</p>
     */
    public UiPropertyConfig<T> visible(boolean visible) {
        this.visible = visible;
        this.visibleOverridden = true;
        return this;
    }

    /**
     * Overrides semantic required-state.
     *
     * <p>Example: {@code cfg.required(true);}</p>
     */
    public UiPropertyConfig<T> required(boolean required) {
        this.required = required;
        this.requiredOverridden = true;
        return this;
    }

    /**
     * Overrides semantic tooltip key.
     *
     * <p>Example: {@code cfg.tooltip("user.email.tooltip");}</p>
     */
    public UiPropertyConfig<T> tooltip(String key) {
        this.tooltipKey = Objects.requireNonNull(key, "key");
        this.tooltipOverridden = true;
        return this;
    }

    /**
     * Returns the wrapped source property.
     *
     * <p>Example: {@code UiProperty<User> property = cfg.property();}</p>
     */
    public UiProperty<T> property() {
        return property;
    }

    /**
     * Returns the effective label key.
     *
     * <p>Example: {@code String key = cfg.labelKey();}</p>
     */
    public String labelKey() {
        return labelKey;
    }

    /**
     * Returns the effective visibility flag.
     *
     * <p>Example: {@code boolean v = cfg.isVisible();}</p>
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Returns the effective required flag.
     *
     * <p>Example: {@code boolean r = cfg.isRequired();}</p>
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Returns the effective tooltip key, if configured.
     *
     * <p>Example: {@code String key = cfg.tooltipKey();}</p>
     */
    public String tooltipKey() {
        return tooltipKey;
    }

    /**
     * Returns whether {@link #label(String)} was explicitly configured.
     *
     * <p>Example: {@code if (cfg.hasLabelOverride()) { ... }}</p>
     */
    public boolean hasLabelOverride() {
        return labelOverridden;
    }

    /**
     * Returns whether {@link #visible(boolean)} was explicitly configured.
     *
     * <p>Example: {@code if (cfg.hasVisibleOverride()) { ... }}</p>
     */
    public boolean hasVisibleOverride() {
        return visibleOverridden;
    }

    /**
     * Returns whether {@link #required(boolean)} was explicitly configured.
     *
     * <p>Example: {@code if (cfg.hasRequiredOverride()) { ... }}</p>
     */
    public boolean hasRequiredOverride() {
        return requiredOverridden;
    }

    /**
     * Returns whether {@link #tooltip(String)} was explicitly configured.
     *
     * <p>Example: {@code if (cfg.hasTooltipOverride()) { ... }}</p>
     */
    public boolean hasTooltipOverride() {
        return tooltipOverridden;
    }
}
