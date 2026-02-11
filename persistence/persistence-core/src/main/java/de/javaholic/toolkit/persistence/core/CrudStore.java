package de.javaholic.toolkit.persistence.core;

import java.util.List;
import java.util.Optional;

public interface CrudStore<T, ID> {

    List<T> findAll();

    Optional<T> findById(ID id);

    T save(T entity);

    void delete(T entity);
}
