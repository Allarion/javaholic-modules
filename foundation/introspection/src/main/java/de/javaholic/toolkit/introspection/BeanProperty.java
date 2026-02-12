package de.javaholic.toolkit.introspection;

import java.lang.reflect.AnnotatedElement;
import java.util.Objects;

public final class BeanProperty<T, V> {
    private final String name;
    private final Class<V> type;
    private final AnnotatedElement definition;

    public BeanProperty(
            String name,
            Class<V> type,
            AnnotatedElement definition
    ) {
        this.name = name;
        this.type = type;
        this.definition = definition;
    }

    public String name() {
        return name;
    }

    public Class<V> type() {
        return type;
    }

    public AnnotatedElement definition() {
        return definition;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (BeanProperty<?,?>) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.definition, that.definition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, definition);
    }

    @Override
    public String toString() {
        return "BeanProperty[" +
                "name=" + name + ", " +
                "type=" + type + ", " +
                "definition=" + definition + ']';
    }

}
