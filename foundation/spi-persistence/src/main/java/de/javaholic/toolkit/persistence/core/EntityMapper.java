package de.javaholic.toolkit.persistence.core;

/**
 * Mapper contract between persistence entity and domain model.
 *
 * <p>Concept: used by persistence adapters to keep JPA/entity details out of domain services.</p>
 *
 * <p>Example:</p>
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
