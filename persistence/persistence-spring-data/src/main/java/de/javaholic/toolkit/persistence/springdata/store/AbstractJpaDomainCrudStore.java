package de.javaholic.toolkit.persistence.springdata.store;

import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.EntityMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Base {@link CrudStore} implementation that adapts a Spring Data {@link JpaRepository}
 * to domain objects via an {@link EntityMapper}.
 *
 * <p>Concept: subclasses provide type binding only; CRUD behavior is shared in this base class.
 * The store boundary remains domain-first, while persistence entities stay internal.</p>
 *
 * <p>Example:</p>
 * <pre>{@code
 * final class JpaUserStore extends AbstractJpaDomainCrudStore<User, UUID, JpaUserEntity, JpaUserRepository> {
 *     JpaUserStore(JpaUserRepository repository, JpaUserMapper mapper) {
 *         super(repository, mapper);
 *     }
 * }
 * }</pre>
 */
// TODO: compare DtoCrudStore -
public abstract class AbstractJpaDomainCrudStore<
        D,        // Domain
        ID,
        E,        // JpaEntity
        R extends JpaRepository<E, ID>
        > implements CrudStore<D, ID> {

    protected final R repository;
    protected final EntityMapper<D, E> mapper;

    /**
     * Creates the adapter with repository and mapper dependencies.
     *
     * <p>Example: {@code super(repository, mapper);}</p>
     */
    protected AbstractJpaDomainCrudStore(R repository, EntityMapper<D, E> mapper) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    /**
     * Loads all entities and maps them into domain instances.
     */
    @Override
    public List<D> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    /**
     * Loads one entity by id and maps it to domain.
     */
    @Override
    public Optional<D> findById(ID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    /**
     * Maps domain to entity, persists it, then maps persisted entity back to domain.
     */
    @Override
    public D save(D entity) {
        E saved = repository.save(mapper.toEntity(entity));
        return mapper.toDomain(saved);
    }

    /**
     * Deletes by mapping the provided domain instance to its entity representation.
     */
    @Override
    public void delete(D entity) {
        repository.delete(mapper.toEntity(entity));
    }
}
