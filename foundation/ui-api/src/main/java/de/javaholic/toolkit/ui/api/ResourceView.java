package de.javaholic.toolkit.ui.api;

public interface ResourceView<T> {

    void create();

    void edit(T item);

    void delete(T item);
}
