package de.javaholic.toolkit.ui.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.Binder;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class Form<T> {

    private final Component layout;
    private final Binder<T> binder;
    private final Map<String, Component> fields;

    Form(Component layout, Binder<T> binder, Map<String, Component> fields) {
        this.layout = layout;
        this.binder = binder;
        this.fields = Collections.unmodifiableMap(new LinkedHashMap<>(fields));
    }

    public Component layout() {
        return layout;
    }

    public Binder<T> binder() {
        return binder;
    }

    public Optional<Component> field(String name) {
        return Optional.ofNullable(fields.get(name));
    }
}
