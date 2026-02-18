package de.javaholic.toolkit.persistence.core;

/**
 * Registry/factory for resolving a {@link CrudStore} by domain type.
 *
 * <p>Example:</p>
 * <pre>{@code
 * CrudStoreFactory factory = ...;
 * CrudStore<User, ?> store = factory.forType(User.class);
 * }</pre>
 */
public interface CrudStoreFactory {

    /**
     * Resolves the store responsible for the given domain type.
     */
    <T> CrudStore<T, ?> forType(Class<T> type);

}
