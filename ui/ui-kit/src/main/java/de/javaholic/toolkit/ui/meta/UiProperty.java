package de.javaholic.toolkit.ui.meta;

import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;

import java.util.Objects;

/**
 * UI semantic property wrapper around a technical {@link BeanProperty}.
 *
 * <p>Responsibilities:</p>
 * <ul>
 * <li>Expose UI defaults ({@code visible}, {@code label}, {@code order})</li>
 * <li>Provide value access via {@link #read(Object)} for render/bind usage</li>
 * </ul>
 *
 * <p>Phase 1 uses defaults only. Annotation-based customizations are planned for Phase 2.</p>
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

    public String name() {
        return beanProperty.name();
    }

    public Class<?> type() {
        return beanProperty.type();
    }

    public Object read(T instance) {
        return readTyped(instance);
    }

    @SuppressWarnings("unchecked")
    public <V> V readTyped(T instance) {
        return beanMeta.getValue((BeanProperty<T, V>) beanProperty, instance);
    }

    public boolean isVisible() {
        return visible;
    }

    public String label() {
        // TODO phase 2: support annotation/i18n-based label resolution.
        return label;
    }

    public int order() {
        // TODO phase 2: support annotation-based ordering.
        return order;
    }
}
