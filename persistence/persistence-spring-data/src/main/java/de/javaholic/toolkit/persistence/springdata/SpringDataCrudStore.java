package de.javaholic.toolkit.persistence.springdata;

import de.javaholic.toolkit.persistence.core.CrudStore;
import de.javaholic.toolkit.persistence.core.CrudStoreMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public class SpringDataCrudStore<T, ID> implements CrudStore<T, ID>, CrudStoreMeta<T> {

    private final Class<T> domainType;
    private final Class<ID> idType;
    private final JpaRepository<T, ID> repository;
    private final EntityIdAccessor<T> idAccessor;

    public SpringDataCrudStore(
            Class<T> domainType,
            Class<ID> idType,
            JpaRepository<T, ID> repository
    ) {
        this.domainType = domainType;
        this.idType = idType;
        this.repository = repository;
        this.idAccessor = new EntityIdAccessor<>(domainType);
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Override
    public Class<T> domainType() {
        return domainType;
    }

    @Override
    public Class<?> idType() {
        return idType;
    }

    @Override
    public Object getId(T entity) {
        return idAccessor.getId(entity);
    }
}
