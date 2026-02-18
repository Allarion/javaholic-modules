package de.javaholic.toolkit.persistence.core;

/**
 * Platform SPI registry for resolving {@link CrudStore} instances by type.
 *
 * <p>This interface supports replaceable adapters and explicit wiring by exposing store lookup
 * as a contract instead of hidden global state.</p>
 *
 * <p><strong>Responsibility</strong></p>
 * <ul>
 *   <li>Resolve a store implementation for a requested model type.</li>
 *   <li>Hide adapter-specific registration details behind an SPI.</li>
 * </ul>
 *
 * <p><strong>Implemented by</strong></p>
 * <ul>
 *   <li>Infrastructure modules that can discover and provide {@link CrudStore} instances.</li>
 * </ul>
 *
 * <p><strong>Used by</strong></p>
 * <ul>
 *   <li>Application wiring code and UI/tooling layers that need dynamic store resolution.</li>
 * </ul>
 *
 * <p><strong>Mode Interaction</strong></p>
 * <ul>
 *   <li>Rapid Mode: resolves entity/domain-oriented stores.</li>
 *   <li>Clean Mode: resolves DTO-oriented stores when DTO adapters are present.</li>
 * </ul>
 *
 * <p>Example usage:</p>
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
