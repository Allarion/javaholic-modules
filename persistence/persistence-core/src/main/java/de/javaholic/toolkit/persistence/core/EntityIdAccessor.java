package de.javaholic.toolkit.persistence.core;

import de.javaholic.toolkit.introspection.BeanMeta;
import de.javaholic.toolkit.introspection.BeanProperty;

import java.util.Optional;

/**
 * Utility for reading id/version values from a domain object using {@link BeanMeta}.
 *
 * <p>Concept: centralize technical id/version extraction so stores/adapters do not duplicate
 * reflection rules.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * BeanMeta<User> meta = BeanIntrospector.inspect(User.class);
 * EntityIdAccessor<User> accessor = new EntityIdAccessor<>(meta);
 * Object id = accessor.getId(user);
 * Optional<Object> version = accessor.getVersion(user);
 * }</pre>
 */
public final class EntityIdAccessor<T> {

    private final BeanMeta<T> meta;
    private final BeanProperty idProperty;
    private final BeanProperty versionProperty;

    /**
     * Creates an accessor from inspected metadata.
     *
     * <p>Throws {@link IllegalStateException} when no id property is available.</p>
     */
    public EntityIdAccessor(BeanMeta<T> meta) {
        this.meta = meta;
        this.idProperty = meta.idProperty().orElseThrow(() -> new IllegalStateException("No @Id field found on " + meta.type().getName()));
        this.versionProperty = meta.versionProperty().orElse(null);
    }

    /**
     * Returns the id value for the given entity.
     */
    public Object getId(T entity) {
        return meta.getValue(idProperty, entity);
    }

    /**
     * Returns the version value if a version property exists.
     */
    public Optional<Object> getVersion(T entity) {
        if (versionProperty == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(meta.getValue(versionProperty, entity));
    }
}
