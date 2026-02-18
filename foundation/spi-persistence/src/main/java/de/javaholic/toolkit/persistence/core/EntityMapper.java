package de.javaholic.toolkit.persistence.core;

/**
 * Platform SPI mapper between persistence entity and domain model.
 *
 * <p>This enforces explicit mapping between layers and prevents persistence annotations/types
 * from leaking into domain models.</p>
 *
 * <p><strong>Responsibility</strong></p>
 * <ul>
 *   <li>Translate persistence entities to domain objects and back.</li>
 *   <li>Keep persistence technology concerns outside bounded-context core code.</li>
 * </ul>
 *
 * <p><strong>Implemented by</strong></p>
 * <ul>
 *   <li>Persistence adapter modules (for example JPA adapters).</li>
 * </ul>
 *
 * <p><strong>Used by</strong></p>
 * <ul>
 *   <li>Persistence stores implementing domain store SPIs.</li>
 * </ul>
 *
 * <p><strong>Mode Interaction</strong></p>
 * <ul>
 *   <li>Rapid Mode: used directly by persistence stores bound to UI via {@link CrudStore}.</li>
 *   <li>Clean Mode: still used in persistence layer; DTO mapping occurs as an additional adapter step.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * final class JpaUserMapper implements EntityMapper<User, JpaUserEntity> {
 *     public User toDomain(JpaUserEntity entity) { ... }
 *     public JpaUserEntity toEntity(User domain) { ... }
 * }
 * }</pre>
 */
public interface EntityMapper<D, E> {

    /**
     * Maps persistence entity to domain model.
     */
    D toDomain(E entity);

    /**
     * Maps domain model to persistence entity.
     */
    E toEntity(D domain);
}
