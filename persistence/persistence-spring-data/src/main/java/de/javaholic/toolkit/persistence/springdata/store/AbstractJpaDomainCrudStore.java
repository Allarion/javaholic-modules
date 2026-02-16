package de.javaholic.toolkit.persistence.springdata.store;

import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.EntityMapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
// FIXME: compare DtoCrudStore here? this class doesnt need to be abstract!
public abstract class AbstractJpaDomainCrudStore<
        D,        // Domain
        ID,
        E,        // JpaEntity
        R extends JpaRepository<E, ID>
        > implements CrudStore<D, ID> {

    protected final R repository;
    protected final EntityMapper<D, E> mapper;

    protected AbstractJpaDomainCrudStore(R repository, EntityMapper<D, E> mapper) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    @Override
    public List<D> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<D> findById(ID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public D save(D entity) {
        E saved = repository.save(mapper.toEntity(entity));
        return mapper.toDomain(saved);
    }

    @Override
    public void delete(D entity) {
        repository.delete(mapper.toEntity(entity));
    }
}
