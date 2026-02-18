package de.javaholic.toolkit.persistence.core;

import java.util.Optional;

/**
 * Platform SPI for metadata about a {@link CrudStore} binding.
 *
 * <p>This contract keeps technical identity/version handling explicit and deterministic while
 * remaining independent from concrete frameworks.</p>
 *
 * <p><strong>Responsibility</strong></p>
 * <ul>
 *   <li>Expose domain and id types for a store.</li>
 *   <li>Provide technical metadata extraction (id/version) without JPA-specific APIs.</li>
 * </ul>
 *
 * <p><strong>Implemented by</strong></p>
 * <ul>
 *   <li>Infrastructure adapters that can introspect mapped objects.</li>
 * </ul>
 *
 * <p><strong>Used by</strong></p>
 * <ul>
 *   <li>Generic CRUD infrastructure and UI tooling that requires technical field handling.</li>
 * </ul>
 *
 * <p><strong>Mode Interaction</strong></p>
 * <ul>
 *   <li>Rapid Mode: typically reads metadata from persistence/domain objects.</li>
 *   <li>Clean Mode: may read metadata from DTO wrappers while underlying domain metadata remains explicit.</li>
 * </ul>
 *
 * <p>Example usage:</p>
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
