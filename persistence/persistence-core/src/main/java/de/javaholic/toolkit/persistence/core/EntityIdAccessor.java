package de.javaholic.toolkit.persistence.core;

import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;

import java.util.Optional;

public final class EntityIdAccessor<T> {

    private final BeanMeta<T> meta;
    private final BeanProperty idProperty;
    private final Optional<BeanProperty> versionProperty;

    public EntityIdAccessor(BeanMeta<T> meta) {
        this.meta = meta;
        this.idProperty = meta.idProperty().orElseThrow(() -> new IllegalStateException("No @Id field found on " + meta.type().getName()));
        this.versionProperty = meta.versionProperty();
    }

    public Object getId(T entity) {
        return meta.getValue(idProperty, entity);
    }

    public Optional<Object> getVersion(T entity) {
        return versionProperty.map(p -> meta.getValue(p, entity));
    }
}
