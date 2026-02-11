package de.javaholic.toolkit.persistence.springdata;

import de.javaholic.toolkit.persistence.core.CrudStore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public class SpringDataCrudStore<T, ID> implements CrudStore<T, ID> {

    private final JpaRepository<T, ID> repository;

    public SpringDataCrudStore(JpaRepository<T, ID> repository) {
        this.repository = repository;
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
}
