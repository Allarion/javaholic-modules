package de.javaholic.toolkit.ui.form;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasLabel;
import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.BeanValidator;
import de.javaholic.toolkit.i18n.I18n;
import de.javaholic.toolkit.ui.form.fields.FieldContext;
import de.javaholic.toolkit.ui.form.fields.FieldRegistry;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

public final class FormBuilder<T> {

    private final Class<T> type;
    private FieldRegistry fieldRegistry = new FieldRegistry();
    private I18n i18n;
    private boolean validationEnabled = true;
    private final Map<String, FieldOverride<T>> overrides = new LinkedHashMap<>();
    private final List<Consumer<Form<T>>> configurators = new ArrayList<>();

    FormBuilder(Class<T> type) {
        this.type = Objects.requireNonNull(type, "type");
    }

    public FormBuilder<T> withFieldRegistry(FieldRegistry fieldRegistry) {
        this.fieldRegistry = Objects.requireNonNull(fieldRegistry, "fieldRegistry");
        return this;
    }

    public FormBuilder<T> withI18n(I18n i18n) {
        this.i18n = Objects.requireNonNull(i18n, "i18n");
        return this;
    }

    public FormBuilder<T> withValidation() {
        this.validationEnabled = true;
        return this;
    }

    public FormBuilder<T> field(String name, Consumer<FieldOverride<T>> spec) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(spec, "spec");
        FieldOverride<T> override = overrides.computeIfAbsent(name, key -> new FieldOverride<>());
        spec.accept(override);
        return this;
    }

    public FormBuilder<T> configure(Consumer<Form<T>> config) {
        Objects.requireNonNull(config, "config");
        configurators.add(config);
        return this;
    }

    public Form<T> build() {
        List<FieldAccess> fields = FieldAccess.forType(type);
        Map<String, FieldAccess> byName = new LinkedHashMap<>();
        for (FieldAccess field : fields) {
            byName.put(field.name(), field);
        }
        failOnUnknownOverrides(byName.keySet());

        VerticalLayout layout = new VerticalLayout();
        Binder<T> binder = new Binder<>(type);
        Map<String, Component> components = new LinkedHashMap<>();

        for (FieldAccess field : fields) {
            FieldOverride<T> override = overrides.get(field.name());
            FieldSpec<T> spec = new FieldSpec<>();
            if (override != null) {
                override.apply(spec);
            }

            Component component = spec.component;
            if (component == null) {
                FieldContext ctx = new FieldContext(
                        type,
                        field.name(),
                        field.type(),
                        field.annotations()
                );
                component = fieldRegistry.create(ctx);
            }

            applyLabel(component, field.name(), spec.label);
            bindField(binder, field, component, spec.validators);

            layout.add(component);
            components.put(field.name(), component);
        }

        Form<T> form = new Form<>(layout, binder, components);
        for (Consumer<Form<T>> config : configurators) {
            config.accept(form);
        }
        return form;
    }

    private void failOnUnknownOverrides(Set<String> fieldNames) {
        for (String key : overrides.keySet()) {
            if (!fieldNames.contains(key)) {
                throw new IllegalArgumentException(
                        "No such property '" + key + "' on " + type.getSimpleName()
                );
            }
        }
    }

    private void applyLabel(Component component, String fieldName, String overrideLabel) {
        if (!(component instanceof HasLabel hasLabel)) {
            return;
        }
        String label = overrideLabel;
        if (label == null) {
            if (i18n != null) {
                label = i18n.text(fieldName);
            } else {
                label = fieldName;
            }
        }
        hasLabel.setLabel(label);
    }

    private void bindField(
            Binder<T> binder,
            FieldAccess field,
            Component component,
            List<Consumer<Binder.BindingBuilder<T, Object>>> validators
    ) {
        if (!(component instanceof HasValue<?, ?>)) {
            throw new IllegalStateException(
                    "Component for property '" + field.name() + "' is not a HasValue"
            );
        }

        @SuppressWarnings("unchecked")
        HasValue<?, Object> value = (HasValue<?, Object>) component;
        Binder.BindingBuilder<T, Object> binding = binder.forField(value);

        if (validationEnabled) {
            binding = binding.withValidator(new BeanValidator(type, field.name()));
        }
        for (Consumer<Binder.BindingBuilder<T, Object>> validator : validators) {
            validator.accept(binding);
        }
        binding.bind(
                bean -> field.get(bean),
                (bean, v) -> field.set(bean, v)
        );
    }

    static final class FieldSpec<T> implements FieldOverride.Applier<T> {
        Component component;
        String label;
        final List<Consumer<Binder.BindingBuilder<T, Object>>> validators = new ArrayList<>();

        @Override
        public void component(Component component) {
            this.component = component;
        }

        @Override
        public void label(String label) {
            this.label = label;
        }

        @Override
        public void validate(Consumer<Binder.BindingBuilder<T, Object>> validator) {
            validators.add(validator);
        }
    }
}
