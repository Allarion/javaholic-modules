package de.javaholic.toolkit.introspection;

import java.lang.reflect.Field;
import java.util.*;

public final class BeanMeta<T> {

    private final Class<T> type;
    private final List<BeanProperty> properties;
    private final Optional<BeanProperty> idProperty;
    private final Optional<BeanProperty> versionProperty;
    private final Map<String, Field> accessors;

    public BeanMeta(Class<T> type, List<BeanProperty> properties, Map<String, Field> accessors, Optional<BeanProperty> idProperty, Optional<BeanProperty> versionProperty) {
        this.type = type;
        this.properties = List.copyOf(properties);
        this.accessors = Collections.unmodifiableMap(new LinkedHashMap<>(accessors));
        this.idProperty = idProperty;
        this.versionProperty = versionProperty;
    }

    public Class<T> type() {
        return type;
    }

    public List<BeanProperty> properties() {
        return properties;
    }

    public Optional<BeanProperty> idProperty() {
        return idProperty;
    }

    public Optional<BeanProperty> versionProperty() {
        return versionProperty;
    }

    public Object getValue(BeanProperty property, Object bean) {
        Field field = accessors.get(property.name());
        if (field == null) {
            throw new IllegalArgumentException("Unknown property: " + property.name());
        }
        try {
            return field.get(bean);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setValue(BeanProperty property, Object bean, Object value) {
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
