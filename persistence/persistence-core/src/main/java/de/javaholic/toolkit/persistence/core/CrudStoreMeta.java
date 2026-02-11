package de.javaholic.toolkit.persistence.core;

public interface CrudStoreMeta<T> {

    Class<T> domainType();

    Class<?> idType();

    Object getId(T entity);

    java.util.Optional<Object> getVersion(T entity);
}
