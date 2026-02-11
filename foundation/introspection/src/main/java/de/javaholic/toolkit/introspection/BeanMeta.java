package de.javaholic.toolkit.introspection;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class BeanMeta<T> {

    private final Class<T> type;
    private final List<BeanProperty> properties;
    private final BeanProperty idProperty;
    private final BeanProperty versionProperty;
    private final Map<String, Field> accessors;

    BeanMeta(
            Class<T> type,
            List<BeanProperty> properties,
            Map<String, Field> accessors,
            BeanProperty idProperty,
            BeanProperty versionProperty
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

    public List<BeanProperty> properties() {
        return properties;
    }

    public Optional<BeanProperty> idProperty() {
        return Optional.ofNullable(idProperty);
    }

    public Optional<BeanProperty> versionProperty() {
        return Optional.ofNullable(versionProperty);
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
