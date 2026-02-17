package de.javaholic.toolkit.persistence.core;

import java.util.Optional;

/**
 * Introspection contract for store metadata.
 *
 * <p>Concept: this exposes technical identity/version information without coupling callers to a
 * specific persistence framework.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * CrudStoreMeta<User> meta = ...;
 * Object id = meta.getId(user);
 * Optional<Object> version = meta.getVersion(user);
 * }</pre>
 */
public interface CrudStoreMeta<T> {

    /**
     * Returns the supported domain type.
     */
    Class<T> domainType();

    /**
     * Returns the id type used by the store.
     */
    Class<?> idType();

    /**
     * Extracts the entity id.
     */
    Object getId(T entity);

    /**
     * Extracts the optimistic-lock version, if available.
     */
    Optional<Object> getVersion(T entity);
}
