package de.javaholic.toolkit.persistence.springdata.store;

import de.javaholic.toolkit.persistence.core.CrudStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class AbstractJpaCrudStore<
        D,        // Domain
        ID,
        E,        // JpaEntity
        R extends JpaRepository<E, ID>
        > implements CrudStore<D, ID> {

    protected final R repository;

    protected AbstractJpaCrudStore(R repository) {
        this.repository = Objects.requireNonNull(repository, "repository");;
    }

    // TODO: maybe wrap in another interface Mapper<d,e> ? Vgl. CrudMode?
    //    enum CrudMode {
    //        RAPID,   // JPA-first defaults
    //        CLEAN    // DTO-first defaults
    //    }
    protected abstract D toDomain(E entity);
    protected abstract E toJpa(D domain);

    @Override
    public List<D> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<D> findById(ID id) {
        return repository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public D save(D entity) {
        E saved = repository.save(toJpa(entity));
        return toDomain(saved);
    }

    @Override
    public void delete(D entity) {
        repository.delete(toJpa(entity));
    }
}