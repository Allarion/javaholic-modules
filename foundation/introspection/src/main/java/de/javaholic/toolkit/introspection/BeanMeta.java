package de.javaholic.toolkit.introspection;

import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public Class<T> type() {
        return type;
    }

    public List<BeanProperty<T, ?>> properties() {
        return properties;
    }

    public Optional<BeanProperty<T, ?>> idProperty() {
        return Optional.ofNullable(idProperty);
    }

    public Optional<BeanProperty<T, ?>> versionProperty() {
        return Optional.ofNullable(versionProperty);
    }

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
