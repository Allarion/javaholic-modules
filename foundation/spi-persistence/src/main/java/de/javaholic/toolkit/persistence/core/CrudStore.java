package de.javaholic.toolkit.persistence.core;

import java.util.List;
import java.util.Optional;

/**
 * Minimal CRUD boundary for one aggregate/root type.
 *
 * <p>Concept: callers work with domain-oriented types while concrete persistence details stay behind
 * the store implementation.</p>
 *
 * <p>Example:</p>
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
