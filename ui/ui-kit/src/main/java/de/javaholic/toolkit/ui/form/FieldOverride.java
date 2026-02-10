package de.javaholic.toolkit.ui.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.data.binder.Binder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class FieldOverride<T> {

    interface Applier<T> {
        void component(Component component);
        void label(String label);
        void validate(Consumer<Binder.BindingBuilder<T, Object>> validator);
    }

    private Component component;
    private String label;
    private final List<Consumer<Binder.BindingBuilder<T, Object>>> validators = new ArrayList<>();

    public FieldOverride<T> component(Component component) {
        this.component = Objects.requireNonNull(component, "component");
        return this;
    }

    public FieldOverride<T> label(String label) {
        this.label = Objects.requireNonNull(label, "label");
        return this;
    }

    public FieldOverride<T> validate(Consumer<Binder.BindingBuilder<T, Object>> validator) {
        validators.add(Objects.requireNonNull(validator, "validator"));
        return this;
    }

    void apply(Applier<T> applier) {
        if (component != null) {
            applier.component(component);
        }
        if (label != null) {
            applier.label(label);
        }
        for (Consumer<Binder.BindingBuilder<T, Object>> validator : validators) {
            applier.validate(validator);
        }
    }
}
