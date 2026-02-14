package de.javaholic.toolkit.introspection;

import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

/**
 * Technical descriptor for one bean property.
 *
 * <p>It contains only name/type/annotation-source metadata and no UI semantics.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * BeanProperty<User, String> prop = new BeanProperty<>("email", String.class, field);
 * System.out.println(prop.name());
 * }</pre>
 */
public final class BeanProperty<T, V> {
    private final String name;
    private final Class<V> type;
    private final AnnotatedElement definition;

    /**
     * Creates a technical property descriptor.
     *
     * <p>Example: {@code new BeanProperty<>("email", String.class, field);}</p>
     */
    public BeanProperty(
            String name,
            Class<V> type,
            AnnotatedElement definition
    ) {
        this.name = name;
        this.type = type;
        this.definition = definition;
    }

    /**
     * Returns the property name.
     *
     * <p>Example: {@code String n = property.name();}</p>
     */
    public String name() {
        return name;
    }

    /**
     * Returns the Java type of the property.
     *
     * <p>Example: {@code Class<?> t = property.type();}</p>
     */
    public Class<V> type() {
        return type;
    }

    /**
     * Returns the annotated source element (field or record component).
     *
     * <p>Example: {@code boolean required = property.definition().isAnnotationPresent(NotNull.class);}</p>
     */
    public AnnotatedElement definition() {
        return definition;
    }

    /**
     * Compares this descriptor by name/type/definition.
     *
     * <p>Example: {@code boolean same = p1.equals(p2);}</p>
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BeanProperty<?,?>) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.definition, that.definition);
    }

    /**
     * Returns a hash code derived from name/type/definition.
     *
     * <p>Example: {@code int hash = property.hashCode();}</p>
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, type, definition);
    }

    /**
     * Returns a debug representation of the descriptor.
     *
     * <p>Example: {@code log.debug(property.toString());}</p>
     */
    @Override
    public String toString() {
        return "BeanProperty[" +
                "name=" + name + ", " +
                "type=" + type + ", " +
                "definition=" + definition + ']';
    }

}
