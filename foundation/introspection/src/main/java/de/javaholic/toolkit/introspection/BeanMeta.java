package de.javaholic.toolkit.introspection;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Technical reflection metadata for a Java bean/record type.
 *
 * <p>Responsibility: provide property descriptors plus typed read/write access against an instance.</p>
 *
 * <p>Must not do: carry UI semantics (visibility, labels, ordering) or any rendering decisions.</p>
 *
 * <p>Architecture fit: {@link BeanIntrospector} produces this as the low-level metadata layer.
 * Higher UI layers (for example {@code UiMeta}) wrap it and add presentation semantics.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * BeanMeta<User> meta = BeanIntrospector.inspect(User.class);
 * BeanProperty<User, ?> first = meta.properties().get(0);
 * Object value = meta.getValue((BeanProperty<User, Object>) first, user);
 * }</pre>
 */
public final class BeanMeta<T> {

    private final Class<T> type;
    private final List<BeanProperty<T, ?>> properties;
    private final BeanProperty<T, ?> idProperty;
    private final BeanProperty<T, ?> versionProperty;
    private final Map<String, Field> accessors;

    BeanMeta(
            Class<T> type,
            List<BeanProperty<T, ?>> properties,
            Map<String, Field> accessors,
            BeanProperty<T, ?> idProperty,
            BeanProperty<T, ?> versionProperty
    ) {
        this.type = type;
        this.properties = List.copyOf(properties);
        this.accessors = Collections.unmodifiableMap(new LinkedHashMap<>(accessors));
        this.idProperty = idProperty;
        this.versionProperty = versionProperty;
    }

    /**
     * Returns the inspected Java type.
     *
     * <p>Example: {@code Class<User> t = meta.type();}</p>
     */
    public Class<T> type() {
        return type;
    }

    /**
     * Returns all technical properties in declaration order.
     *
     * <p>Example: {@code meta.properties().forEach(p -> System.out.println(p.name()));}</p>
     */
    public List<BeanProperty<T, ?>> properties() {
        return properties;
    }

    /**
     * Returns the property marked with {@code @Id}, if present.
     *
     * <p>Example: {@code meta.idProperty().ifPresent(id -> System.out.println(id.name()));}</p>
     */
    public Optional<BeanProperty<T, ?>> idProperty() {
        return Optional.ofNullable(idProperty);
    }

    /**
     * Returns the property marked with {@code @Version}, if present.
     *
     * <p>Example: {@code meta.versionProperty().ifPresent(v -> System.out.println(v.name()));}</p>
     */
    public Optional<BeanProperty<T, ?>> versionProperty() {
        return Optional.ofNullable(versionProperty);
    }

    /**
     * Reads a property value from a bean instance.
     *
     * <p>Example: {@code String name = meta.getValue(nameProperty, user);}</p>
     */
    public <V> V getValue(BeanProperty<T, V> property, T bean) {
        Field field = accessors.get(property.name());
        if (field == null) {
            throw new IllegalArgumentException("Unknown property: " + property.name());
        }
        try {
            return (V) field.get(bean);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes a property value to a bean instance.
     *
     * <p>Example: {@code meta.setValue(nameProperty, user, "Alice");}</p>
     */
    public <V> void setValue(BeanProperty<T, V> property, T bean, V value) {
        Field field = accessors.get(property.name());
        if (field == null) {
            throw new IllegalArgumentException("Unknown property: " + property.name());
        }
        try {
            field.set(bean, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
