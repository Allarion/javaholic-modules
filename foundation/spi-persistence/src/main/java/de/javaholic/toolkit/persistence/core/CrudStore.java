package de.javaholic.toolkit.persistence.core;

import java.util.List;
import java.util.Optional;

/**
 * Platform SPI for CRUD operations on one type.
 *
 * <p>This is generic infrastructure SPI, not a bounded-context contract. It follows the SPI-first
 * and strict dependency-direction principles: domain/core and UI depend on this interface, while
 * adapter modules provide implementations.</p>
 *
 * <p><strong>Responsibility</strong></p>
 * <ul>
 *   <li>Define the minimal read/write contract independent of persistence technology.</li>
 *   <li>Act as the common boundary for both domain-facing and DTO-facing stores.</li>
 * </ul>
 *
 * <p><strong>Implemented by</strong></p>
 * <ul>
 *   <li>Persistence adapters (for example JPA domain stores).</li>
 *   <li>Adapter wrappers such as DTO stores that delegate to domain stores.</li>
 * </ul>
 *
 * <p><strong>Used by</strong></p>
 * <ul>
 *   <li>Domain SPI interfaces that specialize this contract for a bounded context.</li>
 *   <li>UI modules and application services that require CRUD without persistence coupling.</li>
 * </ul>
 *
 * <p><strong>Mode Interaction</strong></p>
 * <ul>
 *   <li>Rapid Mode: UI binds directly to {@code CrudStore<Entity, ID>} provided by persistence adapters.</li>
 *   <li>Clean Mode: UI binds to {@code CrudStore<DTO, ID>} provided by DTO adapters that delegate to domain stores.</li>
 *   <li>Both modes use the same SPI to enforce that Rapid Mode is not a separate architecture.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * CrudStore<User, UUID> store = ...;
 * User saved = store.save(user);
 * Optional<User> loaded = store.findById(saved.getId());
 * }</pre>
 */
public interface CrudStore<T, ID> {

    /**
     * Returns all stored entities.
     */
    List<T> findAll();

    /**
     * Returns one entity by id, if present.
     */
    Optional<T> findById(ID id);

    /**
     * Persists the given entity and returns the persisted state.
     */
    T save(T entity);

    /**
     * Deletes the given entity.
     */
    void delete(T entity);
}
