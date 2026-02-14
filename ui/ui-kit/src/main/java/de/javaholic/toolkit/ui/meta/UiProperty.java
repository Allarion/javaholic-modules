package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;

import java.util.Objects;

/**
 * UI semantic property wrapper around a technical {@link BeanProperty}.
 *
 * <p>Responsibility:</p>
 * <ul>
 * <li>Carry UI defaults ({@code visible}, {@code label}, {@code order}) for one property</li>
 * <li>Provide read access for Grid/Form rendering and binding</li>
 * </ul>
 *
 * <p>Must not do: mutate bean metadata, run reflection discovery, or create UI components.</p>
 *
 * <p>Architecture fit: leaf element of {@link UiMeta}. UI builders consume this type so they stay
 * independent from direct {@link BeanMeta} / reflection APIs.</p>
 *
 * <p>Phase 1 is defaults-only by design. Annotation/custom policy support is deferred to Phase 2.</p>
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
    private final boolean visible;
    private final String label;
    private final int order;

    UiProperty(
            BeanMeta<T> beanMeta,
            BeanProperty<T, ?> beanProperty,
            boolean visible,
            String label,
            int order
    ) {
        this.beanMeta = Objects.requireNonNull(beanMeta, "beanMeta");
        this.beanProperty = Objects.requireNonNull(beanProperty, "beanProperty");
        this.visible = visible;
        this.label = Objects.requireNonNull(label, "label");
        this.order = order;
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
     * Returns whether this property is visible in Phase 1 defaults.
     *
     * <p>Example: {@code if (property.isVisible()) { ... }}</p>
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Returns the UI label text.
     *
     * <p>Example: {@code column.setHeader(property.label());}</p>
     */
    public String label() {
        // TODO phase 2: support @UiLabel (and optional i18n key mapping) instead of name fallback only.
        return label;
    }

    /**
     * Returns the ordering value for this property.
     *
     * <p>Example: {@code int order = property.order();}</p>
     */
    public int order() {
        // TODO phase 2: support @UiOrder instead of Integer.MAX_VALUE default ordering only.
        // TODO phase 2: revisit ordering for dynamic DTO proxy property models.
        return order;
    }
}
