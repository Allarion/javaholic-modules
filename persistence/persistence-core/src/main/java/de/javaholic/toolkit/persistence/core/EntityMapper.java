package de.javaholic.toolkit.persistence.core;

public interface EntityMapper<D, E> {

    D toDomain(E entity);

    E toEntity(D domain);
}
