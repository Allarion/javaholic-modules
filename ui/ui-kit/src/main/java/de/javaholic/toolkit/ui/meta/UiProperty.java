package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;

import java.util.Objects;

/**
 * UI semantic property wrapper around a technical {@link BeanProperty}.
 *
 * <p>Responsibility:</p>
 * <ul>
 * <li>Carry UI semantics ({@code hidden}, {@code technical}, {@code required}, {@code permissionKey},
 * {@code labelKey}, {@code order}, {@code readOnly}) for one property</li>
 * <li>Provide read access for Grid/Form rendering and binding</li>
 * </ul>
 *
 * <p>Must not do: mutate bean metadata, run reflection discovery, or create UI components.</p>
 *
 * <p>Architecture fit: leaf element of {@link UiMeta}. UI builders consume this type so they stay
 * independent from direct {@link BeanMeta} / reflection APIs.</p>
 *
 * <p>UiProperty stores semantic keys only. Text resolution is handled in UI builders through
 * TextResolver and is intentionally outside metadata evaluation.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * UiProperty<User> property = UiInspector.inspect(User.class)
 *     .properties()
 *     .findFirst()
 *     .orElseThrow();
 *
 * String propertyName = property.name();
 * Object value = property.read(user);
 * }</pre>
 */
public final class UiProperty<T> {

    private final BeanMeta<T> beanMeta;
    private final BeanProperty<T, ?> beanProperty;
    private final boolean hidden;
    private final boolean technical;
    private final boolean required;
    private final String permissionKey;
    private final String labelKey;
    private final int order;
    private final boolean readOnly;

    UiProperty(
            BeanMeta<T> beanMeta,
            BeanProperty<T, ?> beanProperty,
            boolean hidden,
            boolean technical,
            boolean required,
            String permissionKey,
            String labelKey,
            int order,
            boolean readOnly
    ) {
        this.beanMeta = Objects.requireNonNull(beanMeta, "beanMeta");
        this.beanProperty = Objects.requireNonNull(beanProperty, "beanProperty");
        this.hidden = hidden;
        this.technical = technical;
        this.required = required;
        this.permissionKey = permissionKey;
        this.labelKey = Objects.requireNonNull(labelKey, "labelKey");
        this.order = order;
        this.readOnly = readOnly;
    }

    /**
     * Returns the technical property name.
     *
     * <p>Example: {@code String key = property.name();}</p>
     */
    public String name() {
        return beanProperty.name();
    }

    /**
     * Returns the Java value type for this property.
     *
     * <p>Example: {@code Class<?> t = property.type();}</p>
     */
    public Class<?> type() {
        return beanProperty.type();
    }

    /**
     * Reads the value from the given instance as {@link Object}.
     *
     * <p>Example: {@code Object v = property.read(user);}</p>
     */
    public Object read(T instance) {
        return readTyped(instance);
    }

    /**
     * Reads the value from the given instance with a typed return.
     *
     * <p>Example: {@code String email = property.readTyped(user);}</p>
     */
    @SuppressWarnings("unchecked")
    public <V> V readTyped(T instance) {
        return beanMeta.getValue((BeanProperty<T, V>) beanProperty, instance);
    }

    /**
     * Returns whether this property is visible.
     *
     * <p>Example: {@code if (property.isVisible()) { ... }}</p>
     */
    public boolean isVisible() {
        return !hidden;
    }

    /**
     * Returns whether this property is marked as hidden by UI semantics.
     *
     * <p>Example: {@code if (property.isHidden()) { ... }}</p>
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Returns whether this property is marked as technical (id/version).
     *
     * <p>Example: {@code if (property.isTechnical()) { ... }}</p>
     */
    public boolean isTechnical() {
        return technical;
    }

    /**
     * Returns whether this property is marked as required.
     *
     * <p>Example: {@code if (property.isRequired()) { ... }}</p>
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Returns an optional permission key annotation value.
     *
     * <p>Example: {@code property.permissionKey().ifPresent(this::checkPermission);}</p>
     */
    public java.util.Optional<String> permissionKey() {
        return java.util.Optional.ofNullable(permissionKey);
    }

    /**
     * Returns the semantic label key.
     *
     * <p>This key is resolved to display text later by a TextResolver.</p>
     *
     * <p>Example: {@code String key = property.labelKey();}</p>
     */
    public String labelKey() {
        return labelKey;
    }

    /**
     * Compatibility alias for {@link #labelKey()}.
     *
     * <p>Example: {@code column.setHeader(property.label());}</p>
     */
    public String label() {
        return labelKey();
    }

    /**
     * Returns the ordering value for this property.
     *
     * <p>Example: {@code int order = property.order();}</p>
     */
    public int order() {
        return order;
    }

    /**
     * Returns whether auto forms should render this property as read-only.
     *
     * <p>Example: {@code if (property.isReadOnly()) { ... }}</p>
     */
    public boolean isReadOnly() {
        return readOnly;
    }
}
