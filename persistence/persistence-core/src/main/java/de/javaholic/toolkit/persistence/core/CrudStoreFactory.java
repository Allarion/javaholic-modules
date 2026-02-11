package de.javaholic.toolkit.persistence.core;

public interface CrudStoreFactory {

    <T> CrudStore<T, ?> forType(Class<T> type);

}
